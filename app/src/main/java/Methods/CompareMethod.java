package Methods;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompareMethod {

    public static boolean deepCompare(JsonNode firstJson, JsonNode secondJson) {

        //checking the equality of the root elements
        if(firstJson.getNodeType() != secondJson.getNodeType()) return false;

        //storing the node type of secondJson
        switch(secondJson.getNodeType()) {

            case BOOLEAN:
            case STRING:
                //The string value of the nodes compared with each other
                return firstJson.asText().equals(secondJson.asText());
            case NUMBER:
                //The double value of the nodes compared with each other
                return firstJson.asDouble() == secondJson.asDouble();
            case ARRAY:
                //First checked if both Array objects of same size
                if(firstJson.size() != secondJson.size()) return false;
                //Declaration and initialisation of Object Array
                List<JsonNode> objectArray = new ArrayList<>();
                //All nodes of secondJson array added to Object Array
                secondJson.forEach(objectArray::add);

                //Iterating through the Array elements of firstJson
                for(int i=0; i < firstJson.size(); ++i) {
                    //findEqual initialised as false
                    boolean findEqual = false;
                    //Each element of firstJson array checked against elements of Object Array
                    for(int j=0; j < objectArray.size(); ++j) {
                        //Checking for exact matches
                        if(deepCompare(firstJson.get(i), objectArray.get(j))) {
                            //If exact match found, findEqual flagged as true
                            findEqual = true;
                            //Matched element removed from Object Array
                            objectArray.remove(j);
                            break;
                        }
                    }
                    //If no matches found for any array element of fistJson, false returned
                    if(!findEqual) return false;
                }
                break;
            case OBJECT:
                //First checked if both objects of same size
                if(firstJson.size() != secondJson.size()) return false;

                //Iterator declared and initialised
                Iterator<String> objectIterator = firstJson.fieldNames();
                //Iterated until end of object elements
                while(objectIterator.hasNext()) {
                    //pointer declared to point to array objects
                    String nameField = objectIterator.next();
                    //if deepCompare() of objects with pointer nameField does not match,
                    //false returned
                    if(!deepCompare(firstJson.get(nameField), secondJson.get(nameField))) {
                        return false;
                    }
                }
                break;
            default:
                throw new IllegalStateException();
        }
        return true;
    }


}
