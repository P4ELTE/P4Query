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
package p4query.experts.verification.pipeline_checking;

import java.util.HashSet;

public class Condition {
    private HashSet<String> valid;
    private HashSet<String> invalid;

    public Condition() {
        valid = new HashSet<String>();
        invalid = new HashSet<String>();
        invalid.add("drop");
    }
    
    public HashSet<String> getValid() { return valid; }

    public HashSet<String> getInvalid() { return invalid; }

    public void addValid (String newValidElement) {
        if (invalid.contains(newValidElement)) { 
            invalid.remove(newValidElement); 
        }
        valid.add(newValidElement);
    }

    public void addInvalid (String newInvalidElement) {
        if (valid.contains(newInvalidElement)) {
            valid.remove(newInvalidElement);
        }
        invalid.add(newInvalidElement);
    }

    public void addValidWithCond (String newValidElement) {
        if (!invalid.contains(newValidElement)) { 
            valid.add(newValidElement);     
        }        
    }

    public void addInvalidWithCond (String newInvalidElement) {
        if (!valid.contains(newInvalidElement)) {
            invalid.add(newInvalidElement);
        }        
    }

    public Boolean hasCond (String id) {
        return valid.contains(id) || invalid.contains(id);
    }

    public String whereCond (String id) {
        if (valid.contains(id)) return "valid";
        else if (invalid.contains(id)) return "invalid";
        else return "";
    }

    public String toString() {
        return ("valid: " + this.valid.toString() + ", invalid: " + this.invalid.toString());
    }

    public Boolean isMatching (Condition other) {
       return (valid.equals(other.getValid()) && invalid.equals(other.getInvalid())); 
    }

    public Boolean isImplies(Condition other) {
        return (this.valid.containsAll(other.getValid()) && this.invalid.containsAll(other.getInvalid()));
    }
}
