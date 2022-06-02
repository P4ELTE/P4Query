#!/usr/bin/env python3
#
# Copyright 2020-2022, Dániel Lukács, Eötvös Loránd University.
# All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Author: Dániel Lukács, 2022
#


import argparse
import subprocess
import os
import sys
from mako.template import Template
from mako.lookup import TemplateLookup
from enum import Enum
from threading import Thread
import errno

## TODO sim, mc, etc.

Goal = Enum('Goal', 'best avg_best avg avg_worst worst')
Mode = Enum('Mode', 'cost sim mc')

DEFAULT_PROGRAM = os.path.abspath('examples/basic_routing-bmv2/basic_routing-bmv2.prism')
DEFAULT_OP_COST = os.path.abspath('examples/basic_routing-bmv2/basic_routing-bmv2.costs_per_cache.prism') 

PRISM_TEMPLATE_PATH = os.path.abspath('templates/stack.prism.mako')
PCTL_TEMPLATE_PATH = os.path.abspath('templates/cost.pctl.mako')

GEN_PRISM_PATH = os.path.abspath("generated/full.prism")
GEN_PCTL_PATH = os.path.abspath("generated/cost.pctl")

def parse_args():
  parser = argparse.ArgumentParser(description='Performance prediction for P4.',  formatter_class=argparse.ArgumentDefaultsHelpFormatter)
  parser.add_argument('program', default=DEFAULT_PROGRAM,
                      help='PRISM file created from P4 program code')
  parser.add_argument('-c', '--costs' , required=True, default=DEFAULT_OP_COST,
                      help='Path of operation cost model for PRISM P4 program')
  parser.add_argument('-p', '--prism', required=True, 
                      help='Path to PRISM directory')
  parser.add_argument('-m', '--mode' , default=Mode.cost.name, choices=Mode._member_names_,
                      help='Choose between cost analysis, simulation, and drawing Markov-chains')
  parser.add_argument('-g', '--goal', default=Goal.avg.name, choices=Goal._member_names_,
                      help='Choose objective for cost analysis. Ignored if mode is not cost analysis. Warning: best and worst are using non-deterministic MDP model, making them 5-10x slower')
  parser.add_argument('-s', '--memsize', nargs='?', required=False, type=int, default=2000,
                      help='Memory size (in cells) of PRISM stack machine')
  parser.add_argument('--pctl', nargs='?', required=False, default=PCTL_TEMPLATE_PATH,
                      help='Custom PCTL formula (useful for testing)')
  parser.add_argument('--prism_javamaxmem', nargs='?', required=False, default='3g',
                      help='Passed as it is to PRISM as -javamaxmem argument')
  parser.add_argument('--prism_maxiters',  nargs='?', required=False, default='200000',
                      help='Passed as it is to PRISM as -maxiters argument')

  args = parser.parse_args()

  return args

def assert_file(filepath):
  if not os.path.isfile(filepath):
    raise FileNotFoundError(
        errno.ENOENT, os.strerror(errno.ENOENT), filepath)

def assert_dir(filepath):
  if not os.path.isdir(filepath):
    raise NotADirectoryError(
        errno.ENOENT, os.strerror(errno.ENOENT), filepath)

def extract_program_source(program_source, is_nondet):
    program_source = os.path.abspath(program_source)

    if is_nondet: 
        print('Non-deterministic goal selected, adding -nondet prefix to file name.',file=sys.stderr)
        program_source = os.path.splitext(program_source)[0]+ '-nondet.prism'

    assert_file(program_source)
    return program_source


def process_args(args):
    args2 = args

    args2.is_nondet = Goal[args.goal] in [Goal.best, Goal.worst]

    args2.model_type = 'mdp' if args2.is_nondet else 'dtmc'

    args2.program_source = extract_program_source(args.program, args2.is_nondet)

    args2.op_cost = os.path.abspath(args.costs)
    assert_file(args2.op_cost)

    assert_dir(args.prism)

    args2.prism_exec_path = os.path.join(args.prism, 'prism', 'bin', 'prism')
    assert_file(args2.prism_exec_path)

    args2.mode = Mode[args.mode]
    args2.goal = Goal[args.goal]

    return args2


def build_prism_cmd(args):
    return (args.prism_exec_path, '--explicit', '-maxiters', args.prism_maxiters, '-javamaxmem', args.prism_javamaxmem, GEN_PRISM_PATH, GEN_PCTL_PATH)

def generate_prism_output(args):

    os.makedirs(os.path.dirname(GEN_PRISM_PATH), exist_ok=True)
    with open(PRISM_TEMPLATE_PATH) as f:
        # Note: With /, it expects absolute paths. With ., it expects relative paths
        tmpl = Template(f.read(), lookup = TemplateLookup(directories=['/']))

        rendered = tmpl.render(
            model_type = args.model_type,
            memsize = args.memsize,
            program_source = args.program_source,
            operator_cost_model = args.op_cost )

        with open(GEN_PRISM_PATH, "w+") as full_prism:
            print(rendered, file=full_prism)

def generate_pctl_output(args):

    os.makedirs(os.path.dirname(GEN_PCTL_PATH), exist_ok=True)

    with open(args.pctl) as f:
        tmpl = Template(f.read(), lookup = TemplateLookup(directories=['/']))

        rendered = tmpl.render(
            Goal = Goal,
            goal = args.goal)

        with open(GEN_PCTL_PATH, "w+") as pctl:
            print(rendered, file=pctl)

def print_proc_stream(proc_stream, out_stream):
    with proc_stream:
        for line in iter(proc_stream.readline, ''):
            print(line,end='',file=out_stream)

def run_prism(cmd):
    print(cmd, file=sys.stderr)

    with subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE,bufsize=1, universal_newlines=True) as p:

        Thread(target=print_proc_stream, args=[p.stdout, sys.stdout]).start()
        Thread(target=print_proc_stream, args=[p.stderr, sys.stderr]).start()

        errcode = p.wait()
        if errcode != 0:
          print("Error code was %d for process %s" % (errcode, str(cmd)), file=sys.stderr)

#    print(out.decode("utf-8"),file=sys.stderr)
#    print(err.decode("utf-8"),file=sys.stderr)



args = process_args(parse_args())

generate_prism_output(args)

generate_pctl_output(args)

cmd = build_prism_cmd(args)

run_prism(cmd)



