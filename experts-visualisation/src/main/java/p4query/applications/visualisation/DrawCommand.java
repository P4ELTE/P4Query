/**
 * Copyright 2020-2021, Eötvös Loránd University.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package p4query.applications.visualisation;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import p4query.ontology.providers.AppUI;

@Parameters(commandDescription = "Launch the graph drawing application")
public class DrawCommand extends AppUI {


    @Override
    public String getCommandName() {
        return "draw";
    }

    @Override
    public String[] getCommandNameAliases() {
        return new String[]{};
    }

    @Parameter(names = { "-A", "--analysers"}, description = "<descriptors of analysers whose results will be included in the output subgraph>", validateWith = OptionCannotBeValueValidator.class, variableArity = true)
    public List<String> names;

    @Parameter(names = { "-F", "--format"}, description="Output subgraph file format descriptors (e.g. svg).", validateWith = OptionCannotBeValueValidator.class)
    public List<String> format;

    @Parameter(names = { "-o", "--output"}, description="Preferred location of output subgraph file.", validateWith = OptionCannotBeValueValidator.class)
    public Boolean output = false;

    @Override
    public String toString() {
        return "DrawCommand [super= " + super.toString()+ ", format=" + format + ", help=" + help + ", output=" + output + ", names=" + names
                + "]";
    }
}