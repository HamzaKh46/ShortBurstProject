package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
public class WebScraping {
    public static void main(String[] args) {
        String baseURL = "https://www.immoweb.be";
        String startURL = baseURL + "/fr/recherche/immeuble-mixte/a-vendre";
        Set<String> uniqueLinks = new HashSet<>();
        boolean hasNextPage = true;

        try {
            String currentURL = startURL;

            while (hasNextPage) {
                System.out.println("Fetching page: " + currentURL);
                // Fetch and parse the page
                Document doc = Jsoup.connect(currentURL).get();

                // Extract property links
                Elements propertyLinks = doc.select("a.card__title-link");
                for (Element link : propertyLinks) {
                    String fullLink = link.attr("abs:href"); // Get the full URL
                    uniqueLinks.add(fullLink);
                }

                // Find the "Next" page link
                Element nextPageElement = doc.selectFirst("a.pagination__link--next");
                if (nextPageElement != null) {
                    currentURL = nextPageElement.attr("abs:href");
                } else {
                    hasNextPage = false;
                }

                // Pause between requests to avoid overloading the server
                Thread.sleep(1000); // 1-second delay
            }

            // Save the links to a file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("property_links.txt"))) {
                for (String link : uniqueLinks) {
                    writer.write(link);
                    writer.newLine();
                }
            }

            System.out.println("Scraping completed. Links saved to property_links.txt.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
