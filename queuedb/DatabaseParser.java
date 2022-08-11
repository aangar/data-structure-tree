package queuedb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import queuedb.Objects.DatabaseDocument;

/**
 * A Class to read documents from a Collection,
 * then convert them into the target document.
 * 
 * @author aangar, 2022
 */
public class DatabaseParser<T extends DatabaseDocument> {

    /**
     * Target Class Type. This can be anything <b>that extends DatabaseDocument</b>
     */
    private Class<T> clazz;
    /**
     * The returnable class field. This will be set once <b>buildClazz</b> has been
     * called. <b>Do not modify this directly!</b>
     */
    private T returnable;
    /**
     * A list of Fields found in the target Class. Since any sort of extended fields
     * will be left out.
     */
    private List<String> fieldNames;

    /**
     * Name is self explanatory. Come on now.
     */
    private String DIR_TO_COLLECTION;

    /**
     * Default constructor for the DatabaseParser.
     * 
     * @param clazz the target class.
     */
    public DatabaseParser(Class<T> clazz, String dir) {
        this.clazz = clazz;
        this.DIR_TO_COLLECTION = dir;
    }

    /**
     * Builds a class for the target document to convert to. This must be called
     * before trying to set fields parsed.
     */
    private void buildClazz() {
        try {
            Constructor<?> s[] = this.clazz.getDeclaredConstructors();
            Field[] fields = this.clazz.getDeclaredFields();
            List<String> allowedFields = new ArrayList<>();
            allowedFields.add("id");
            for (Field f : fields) {
                String name = f.toString();
                int end = name.lastIndexOf(".") + 1;
                allowedFields.add(name.substring(end, name.length()));
            }
            this.fieldNames = allowedFields;
            this.returnable = (T) s[0].newInstance();
        } catch (InvocationTargetException nsm) {

        } catch (InstantiationException ie) {

        } catch (IllegalAccessException iae) {

        }

    }

    /**
     * Reads the file from the filename, and returns the parsed object.
     * 
     * @param file the path to the file, absolute.
     * @return the parsed file into the target object.
     */
    public T readFile(String file) {
        this.returnable = null;
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;
            buildClazz();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                String trimmy = line.trim();
                int ind = trimmy.indexOf("\"", 1);
                if (ind > 0) {
                    String propName = trimmy.substring(1, ind);
                    if (this.fieldNames.contains(propName)) {
                        int startQuoteInd = trimmy.indexOf("\"", ind + 1) + 1;
                        int endQuoteInd = trimmy.indexOf("\"", startQuoteInd + 1);
                        String val = trimmy.substring(startQuoteInd, endQuoteInd);
                        this.returnable.setParsedProperty(propName, val);
                    }
                }
                sb.append("\n");
            }
            fr.close();
        } catch (IOException e) {
            System.out.println("There was an error parsing a document.");
            System.out.println(e);
        }

        return this.returnable;
    }

    /**
     * Finds all documents in the collection.
     * 
     * @return found documents.
     */
    public List<T> findAll() {
        List<String> documents = List.of(new File(this.DIR_TO_COLLECTION).list());
        if (documents.isEmpty()) {
            System.out.println("No documents in collection.");
            return new ArrayList<>();
        }

        return documents.stream().map(doc -> {
            this.readFile(this.DIR_TO_COLLECTION + doc);
            return this.returnable;
        }).collect(Collectors.toList());
    }
}
