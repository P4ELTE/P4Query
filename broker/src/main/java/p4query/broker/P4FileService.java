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
package p4query.broker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codejargon.feather.Provides;

import p4query.ontology.providers.P4FileProvider.InputP4File;
import p4query.ontology.providers.P4FileProvider.P4Include;

class P4FileService {
    private File inputP4;
//    Map<String, File> includes; // this is not used, feel free to delete it
    private List<String> includeDirs;

    public P4FileService(String inputP4, List<String> includeDirs) throws IOException {
        this.inputP4 = toFile(inputP4);
        this.includeDirs = includeDirs;

//        this.includes = 
//            Files.find(includeDir.toPath(), 999, (p, bfa) -> bfa.isRegularFile())
//                .collect(
//                    Collectors.toMap(
//                        p -> includeDir.toPath().relativize(p).toString(), 
//                        p -> toFile(p.toString())));
    }

    private static File toFile(String fileName)  {
        File p4File = new File(fileName);
        if(!p4File.exists() || !p4File.isFile()){
            throw new IllegalArgumentException("Cannot find P4 file at location " + App.absolutePath(fileName));
        }
        return p4File;
    }

    @Provides @InputP4File
    public File inputP4(){ return inputP4; }

    @Provides @P4Include
    public List<String> includeDirs(){ 
        return includeDirs; 
    }
}