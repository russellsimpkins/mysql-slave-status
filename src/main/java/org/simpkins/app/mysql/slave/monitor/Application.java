package org.simpkins.app.mysql.slave.monitor;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
/**
 * This console application reads a json file to get the IP/Hostname of all MySql
 * slaves. It then executes "show slave status" and gets the "Number of Seconds
 * Behind" to generates a json file that can be monitored and viewed from a
 * dashboard. The application.properties externalize our dependencies.
 *  
 * There are a few points where this can fail:
 * 
 * - bad slaves.json path or bad permissions
 * - bad database information
 * - bad resultsFile location specified or bad permissions
 * 
 * The app uses SpringBoot
 * 
 * @author Russell Simpkins <russellsimpkins at gmail.com>
 */
public class Application {

    /**
     * Here is where all the magic happens.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = new SpringApplication(Application.class).run(args);
        Database d = ctx.getBean(Database.class);
        ArrayList<String> slaves = d.getDbSlaves();

        // this is the desired json output structure
        // {"slaves": [{ "slave":"1.1.1.1", "seconds_behind": 0}]}
        HashMap<String, ArrayList<Status>> results = new HashMap<>();
        ArrayList<Status> items = new ArrayList<>();
        results.put("slaves", items);

        if (slaves != null) {
            for (String slave : slaves) {
                SqlSessionFactory sessionFactory = d.getSessionForHost(slave, ctx.getEnvironment());
                SqlSession sess = sessionFactory.openSession();
                StatusMapper sm = sess.getMapper(StatusMapper.class);
                Status status = sm.selectSlaveStatus();
                if (status != null) {
                    status.setHost(slave);
                    items.add(status);
                }
                sess.close();
            }
        } else {
            System.out.println("slaves null");
        }

        // create a json output string
        Gson gson = new Gson();
        String output = gson.toJson(results);

        System.out.println(output);
        Application.writeResults(ctx.getEnvironment().getProperty("resultsFile"), output);
        ctx.close();
    }

    /**
     * A utility function used to write json to an output file. Only useful for
     * smaller sets contained in a string. Not what you want to do for very
     * large string output.
     * 
     * @param dest
     *            - String name of the <path>/<filename> to write to
     * @param output
     *            - String json encoded data to write.
     * @throws FileNotFoundException
     *             - thrown if you specify a bad path or you don't have correct
     *             permissions
     */
    public static void writeResults(String dest, String output) throws FileNotFoundException {

        PrintWriter out = new PrintWriter(dest);
        out.print(output);
        out.close();
    }
}
