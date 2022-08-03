package Controller;

import Methods.CompareMethod;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import static Methods.CompareMethod.deepCompare;

@Controller
public class FileUploadController {

    //Defining the upload directory
    public static String uploadDirectory = System.getProperty("user.dir")+"\\src\\uploads";

    //Mapping HTTP request to MVC Handler
    @RequestMapping("/")
    public String uploadPage(Model model){

        //Defining the project directory to which files being uploaded
        File directoryPath = new File("src/uploads");
        for(File file: directoryPath.listFiles())
            if (!file.isDirectory())
                //Clearing any existing files in project directory
                file.delete();
        //rending uploadview.html to the HTTP End Point
        return "uploadview";
    }

    //Mapping HTTP request to MVC Handler
    @RequestMapping("/upload")
    public String upload(Model model, @RequestParam("files")MultipartFile[] files){
        //Variable for storing file names of uploaded files
        StringBuilder fileNames = new StringBuilder();
        for(MultipartFile file: files){
            //Adding file names to variable
            Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
            fileNames.append(file.getOriginalFilename()+" ");
            try{
                //Copying uploaded files to Project Directory
                Files.write(fileNameAndPath,file.getBytes());

                File folder = new File("/src/uploads");
                File[] listOfFiles = folder.listFiles();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Adding message to the user after upload
        model.addAttribute("msg", "Successfully uploaded files : "+ fileNames.toString());
        //rendering uploadstatusview.html to the HTTP End Point
        return "uploadstatusview";
    }

    //Mapping HTTP request to MVC Controller
    @RequestMapping("/compare")
    public String compareFiles(Model model) throws IOException, JsonParseException {

        StringBuilder messages = new StringBuilder();

        File directoryPath = new File("src/uploads");
        //List of all files and directories
        String contents[] = directoryPath.list();

        //If the directory has less than two files in the folder
        if(contents.length <= 1){
            messages.append("Not enough files uploaded for comparison");
            model.addAttribute("message",messages);
            return "compareview";
        }

        //If the directory has more than two files in the folder
        if(contents.length > 2){
            messages.append("More than two files uploaded for comparison");
            model.addAttribute("message",messages);
            return "compareview";
        }

        //Files in the specified directory printed in terminal
        System.out.println("List of files and directories in the specified directory:");
        for(int i=0; i<contents.length; i++) {
            System.out.println(contents[i]);
        }

        String path1 = contents[0];
        String path2 = contents[1];

        //Initialising JsonNode objects
        JsonNode fJSON = null, sJSON = null;

        //Initialising deep compare and shallow compare check values
        boolean val1 = false, val2 = false;

        ObjectMapper mapper = new ObjectMapper();

        //Assigning first uploaded file as File A
        File fileA = new File("src/uploads/"+path1);

        //Checking file existence
        if (!fileA.exists()) {
            System.out.println("File A missing or incorrectly specified");
            messages.append("File A missing or incorrectly specified\n");
        }

        String fileName = fileA.toString();

        //Checking File Extension
        String extension = com.google.common.io.Files.getFileExtension(fileName);
        System.out.println(extension);

        if((!extension.equals("json")) && (!extension.equals("geojson"))){
            messages.append("File A: Not a valid Json or GeoJson file\n");
            messages.append("Extension of File A : "+ extension);
            System.out.println("File A: Not a valid Json or GeoJson file");
        }





        try{
            //Parsing File A as JsonNode
            fJSON = mapper.readTree(fileA);
            System.out.println("File A has a valid JSON syntax");
            messages.append("File A has a valid JSON syntax\n");
        } catch (JsonParseException e) {
            System.out.println("File A does not have a valid JSON syntax");
            messages.append("File A does not have a valid JSON syntax\n");
        }



        //Assigning second uploaded file as File B
        File FileB = new File("src/uploads/"+path2);

        //Checking File Existence
        if (!FileB.exists()) {
            System.out.println("File B missing or incorrectly specified");
            messages.append("File B missing or incorrectly specified\n");
        }

        //Checking File Extension
        fileName = FileB.toString();
        extension = com.google.common.io.Files.getFileExtension(fileName);
        System.out.println(extension);

        if((!extension.equals("json")) && (!extension.equals("geojson"))){
            System.out.println("File B : Not a valid Json or GeoJson file");
            messages.append("File B : Not a valid Json or GeoJson file\n");
            messages.append("Extension of File B : "+ extension);
        }



        try{
            //Parsing File B as JsonNode
            sJSON = mapper.readTree(FileB);
            System.out.println("File B has a valid JSON syntax");
            messages.append("File B has a valid JSON syntax\n");
        } catch (JsonParseException e) {
            System.out.println("File B does not have a valid JSON syntax");
            messages.append("File B does not have a valid JSON syntax\n");
        }



        try {

            //Deep comparing first and second JsonNode objects
            val1 = deepCompare(fJSON, sJSON);
            System.out.println(deepCompare(fJSON, sJSON));

            //String comparing first and second JsonNode objects
            val2 = fJSON.equals(sJSON);
            System.out.println(fJSON.equals(sJSON));

            //If both Deep and String comparison results returned true
            if (val1 && val2) {
                System.out.println("Objects same and both in same order");
                messages.append("Objects same and both in same order\n");
            }
            //If Deep comparison result is true and String Comparison result is false
            else if (val1 && !val2) {
                System.out.println("Objects same but both not in same order");
                messages.append("Objects same but both not in same order\n");
            }
            //If both Deep and String Comparison results are false
            else if (!val1 && !val2) {
                System.out.println("Objects not same");
                messages.append("Objects not same\n");
            }

            //Calculating and printing number of objects in File A and B
            System.out.println("Number of objects in file A: "+fJSON.size());
            messages.append("Number of objects in file A: "+fJSON.size()+"\n");
            System.out.println("Number of objects in file B: "+sJSON.size());
            messages.append("Number of objects in file B: "+sJSON.size()+"\n");

        } catch (Exception e) {
            System.out.println("JSON files not valid for comparison");
            messages.append("JSON files not valid for comparison\n");
        }


        //Clearing the upload directory after comparison
        for(File file: directoryPath.listFiles())
            if (!file.isDirectory())
                file.delete();



        model.addAttribute("message",messages);

        //Rendering the compareview.html webpage to HTTP Request
        return "compareview";
    }
}
