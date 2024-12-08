package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import weka.core.Instance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PropertyScraper {

    public static void main(String[] args) {

        String url = "https://www.immoweb.be/fr/annonce/maison-de-campagne/a-vendre/londerzeel/1840/20376940"; // Replace with the property URL

        try {
            // Fetch the page content
            Document doc = Jsoup.connect(url).get();

            // Extracting required fields
            String price = extractDetailByClass(doc,"classified__price");
            String bedrooms = extractDetail(doc, "Chambres");
            String bathrooms = extractDetail(doc, "Salles de bains");
            String sqftLiving = extractDetail(doc, "Surface habitable");
            String sqftLot = extractDetail(doc, "Surface du terrain");
            String floors = extractDetail(doc, "Nombre d'étages");
            String waterfront = extractDetail(doc, "Front de mer");
            String view = extractDetail(doc, "Vue");
            String condition = extractDetail(doc, "État du bâtiment");
            String sqftAbove = extractDetail(doc, "Surface habitable au-dessus du sol");
            String sqftBasement = extractDetail(doc, "Surface habitable du sous-sol");
            String yrBuilt = extractDetail(doc, "Année de construction");
            String yrRenovated = extractDetail(doc, "Année de renovation");

            // Encode the condition
            int encodedCondition = encodeCondition(condition);

            // Create the Property object
            writeToCSV(cleanPrice(price), bedrooms, bathrooms, cleanArea(sqftLiving), cleanArea(sqftLot), floors, waterfront, view,
                    encodedCondition, sqftAbove, sqftBasement, yrBuilt, yrRenovated);


            // You can now pass this property object to your model

            // If you have a model to predict, you can pass this property object to the model's prediction method
            // Example: model.predict(property);

        } catch (IOException e) {
            System.err.println("Error fetching data for the property.");
            e.printStackTrace();
        }
    }

    private static String extractDetail(Document doc, String label) {
        Elements rows = doc.select(".classified-table__row");
        for (Element row : rows) {
            String header = row.select(".classified-table__header").text();
            if (header.equalsIgnoreCase(label)) {
                return row.select(".classified-table__data").text();
            }
        }
        return "0";
    }

    private static String extractDetailByClass(Document doc, String className) {
        Elements elements = doc.select("." + className);
        if (!elements.isEmpty()) {
            String rawText = elements.first().text();
            String[] parts = rawText.split("\\s+");
            return parts[0];
        }
        return null;
    }

    public static int encodeCondition(String condition) {
        Map<String, Integer> conditionMap = new HashMap<>();
        conditionMap.put("À démolir", 1);
        conditionMap.put("À rénover", 2);
        conditionMap.put("À rafraîchir", 3);
        conditionMap.put("Bon état", 4);
        conditionMap.put("Excellent état", 5);
        return conditionMap.getOrDefault(condition, 0);
    }

    public static void writeToCSV(String price, String bedrooms, String bathrooms, String sqftLiving,
                                  String sqftLot, String floors, String waterfront, String view,
                                  int condition, String sqftAbove, String sqftBasement,
                                  String yrBuilt, String yrRenovated) {
        try {
            // Define the CSV file path
            String csvFilePath = "src/main/resources/property_data.csv";
            FileWriter writer = new FileWriter(csvFilePath, true); // true means append to the file

            // Write the header if the file is empty
            writer.append("price,bedrooms,bathrooms,sqft_living,sqft_lot,floors,waterfront,view,condition,sqft_above,sqft_basement,yr_built,yr_renovated\n");

            // Write the property data
            writer.append(price).append(",")
                    .append(bedrooms).append(",")
                    .append(bathrooms).append(",")
                    .append(sqftLiving).append(",")
                    .append(sqftLot).append(",")
                    .append(floors).append(",")
                    .append(waterfront).append(",")
                    .append(view).append(",")
                    .append(String.valueOf(condition)).append(",")
                    .append(sqftAbove).append(",")
                    .append(sqftBasement).append(",")
                    .append(yrBuilt).append(",")
                    .append(yrRenovated).append("\n");

            // Close the file writer
            writer.flush();
            writer.close();
            System.out.println("Property data written to CSV file.");

        } catch (IOException e) {
            System.err.println("Error writing to CSV file.");
            e.printStackTrace();
        }
    }

    private static String cleanPrice(String price) {
        // Replace non-breaking spaces (U+202F) and other special characters with an empty string
        price = price.replaceAll("[^0-9]", "");  // Remove anything that's not a number

        // Optionally, you can add commas or periods if needed, depending on your locale
        // For example, if the price has commas, you can replace them:
        // price = price.replaceAll(",", "");

        return price;
    }

    private static String cleanArea(String area) {
        // Replace non-numeric characters (including spaces and letters) and keep only numbers
        area = area.replaceAll("[^0-9]", "");  // Keep only numeric characters

        return area;
    }


}



