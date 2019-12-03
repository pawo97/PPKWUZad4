package com.example.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.property.StructuredName;

@Controller
public class WebController {

    public List<Employee> empList;

    @GetMapping("/employees/{name}")
    public String getPeople(@PathVariable String name, Model theModel) throws Exception {
        Document document = Jsoup.parse(sendGet(name));
        int i = 0;
        Elements people = document.select("div.user-profile-container");
        empList = new ArrayList<>();
        Elements per = document.select("div.user-info");
        for (Element n : per) {

            Elements per1 = n.select("h3");
            String[] pom = per1.toString()
                               .split(">");
            String firstName = pom[2].toString()
                                     .substring(0, pom[2].toString()
                                                         .length()
                                                   - 3);
            Elements per2 = n.select("h4");
            String secondName = per2.toString()
                                    .substring(4, per2.toString()
                                                      .length()
                                                  - 5);
            if (secondName.equals(new String("&nbsp;"))) {
                secondName = "";
            }
            empList.add(new Employee(i, firstName, secondName, 0));
            i++;
        }

        theModel.addAttribute("employees", empList);
        return "login";
    }

    @GetMapping("/showFormForUpdate")
    @ResponseBody
    public String showFormForUpdate(@RequestParam("employeeId") int theId, Model theModel, HttpServletRequest request,
            HttpServletResponse response) {
        Employee theEmployee = null;
        // get the employee from the service
        for (Employee e : empList) {
            if (e.id == theId) {
                theEmployee = e;
            }
        }
        VCard vcard = new VCard();

        StructuredName n = new StructuredName();
        String[] s = theEmployee.firstName.split(" ");
        n.setFamily(s[0]);
        n.setGiven(s[1]);
        n.getPrefixes()
         .add(theEmployee.lastName);
        vcard.setStructuredName(n);

        vcard.setFormattedName(theEmployee.firstName);

        String str = Ezvcard.write(vcard)
                            .version(VCardVersion.V4_0)
                            .go();

        try {
            saveVctfFile(request, response, str, vcard);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return str;
    }

    public void saveVctfFile(HttpServletRequest request, HttpServletResponse response, String content, VCard vcard) throws IOException {
        File f = new File("contact.vcf");
        FileOutputStream fop = new FileOutputStream(f);

        if (f.exists()) {
            String str = content;
            fop.write(str.getBytes());

            BufferedReader br = null;
            String sCurrentLine;
            br = new BufferedReader(new FileReader("contact.vcf"));
            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
            }

            String dataDirectory = request.getServletContext()
                                          .getRealPath("/WEB-INF/downloads/pdf/");
            Path file = Paths.get(dataDirectory, "contact.vcf");
            if (Files.exists(file)) {
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "attachment; filename=" + "contact.vcf");
                try {
                    Files.copy(file, response.getOutputStream());
                    response.getOutputStream()
                            .flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            File myfile = new File("employee.vcf");
            Ezvcard.write(vcard)
                   .go(myfile);
            InputStream myStream = new FileInputStream(myfile);

            IOUtils.copy(myStream, response.getOutputStream());
            response.addHeader("Content-disposition", "attachment;filename=employee.vcf");
            response.setContentType("application/vcf");

            fop.flush();
            fop.close();
            System.out.println("The data has been written");
        } else {
            System.out.println("This file does not exist");
        }
    }

    private static String sendGet(String name) throws Exception {

        String url = "https://adm.edu.p.lodz.pl/user/users.php?search=" + name + "&x=0&y=0";

        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

        httpClient.setRequestMethod("GET");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = httpClient.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }

    }
}
