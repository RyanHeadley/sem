package com.napier.sem;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.sql.*;
import java.util.ArrayList;

public class App
{
    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        // Get City By City Name
        City city = a.getCity("Edinburgh");
        a.displayCity(city);

        // Get Country By Country Code
        Country country = a.getCountry("GBR");
        a.displayCountry(country);

        // Get Capital City By Country Name
        City capitalCity = a.getCapitalCity("France");
        a.displayCapitalCity(capitalCity);

        // Get Country Populations By Country Name
        Country countryPop = a.getCountryPopulation("Spain");
        a.displayCountryPopulation(countryPop);

        // Get Region Populations By Region Name
        Country regionPop = a.getRegionPopulation("Caribbean");
        a.displayRegionPopulation(regionPop);

        // Get Continent Populations By Continent Name
        Country continentPop = a.getContinentPopulation("Europe");
        a.displayContinentPopulation(continentPop);


        // Lab 4

        // Extract employee salary information
       // ArrayList<Employee> employees = a.getAllSalaries();

        // Test the size of the returned data - should be 240124
       // System.out.println(employees.size());

        
        // Disconnect from database
        a.disconnect();
    }

        /**
         * Connection to MySQL database.
         */
        private Connection con = null;

        /**
         * Connect to the MySQL database.
         */
        public void connect()
        {
            try
            {
                // Load Database driver
                Class.forName("com.mysql.jdbc.Driver");
            }
            catch (ClassNotFoundException e)
            {
                System.out.println("Could not load SQL driver");
                System.exit(-1);
            }

            int retries = 10;
            for (int i = 0; i < retries; ++i)
            {
                System.out.println("Connecting to database...");
                try
                {
                    // Wait a bit for db to start
                    Thread.sleep(30000);
                    // Connect to database
                    con = DriverManager.getConnection("jdbc:mysql://db:3306/world?useSSL=false", "root", "example");
                    System.out.println("Successfully connected");
                    break;
                }
                catch (SQLException sqle)
                {
                    System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                    System.out.println(sqle.getMessage());
                }
                catch (InterruptedException ie)
                {
                    System.out.println("Thread interrupted? Should not happen.");
                }
            }
        }

        /**
         * Disconnect from the MySQL database.
         */
        public void disconnect()
        {
            if (con != null)
            {
                try
                {
                    // Close connection
                    con.close();
                }
                catch (Exception e)
                {
                    System.out.println("Error closing connection to database");
                }
            }
        }

        public City getCity(String name)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT ID, Name, CountryCode, District, Population "
                            + "FROM city "
                            + "WHERE Name LIKE '" + name + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new city if valid.
            // Check one is returned
            if (rset.next())
            {
                City city = new City();
                city.city_ID = rset.getInt("ID");
                city.city_name = rset.getString("Name");

                Country country = getCountryForCity(rset.getString("CountryCode"));
                city.countryName = country.country_name;

                city.district = rset.getString("District");
                city.population = rset.getInt("Population");

                return city;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get city details");
            return null;
        }
    }

    public void displayCity(City city)
    {
        if (city != null)
        {
            System.out.println(
                    city.city_name + " "
                            + city.countryName + " "
                                + city.district + " "
                                    + city.population);
        }
        else
        {
            System.out.println("No city found");
        }
    }

    public Country getCountry(String code)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT Code, Name, Continent, Region, Population, Capital "
                            + "FROM country "
                            + "WHERE Code LIKE '" + code + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new country if valid.
            // Check one is returned
            if (rset.next())
            {
                Country country = new Country();
                country.country_code = rset.getString("Code");
                country.country_name = rset.getString("Name");
                country.continent = rset.getString("Continent");
                country.region = rset.getString("Region");
                country.population = rset.getInt("Population");

                City city = getCityForCountry(rset.getInt("Capital"));
                country.capitalName = city.city_name;

                return country;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get country details");
            return null;
        }
    }

    public void displayCountry(Country country)
    {
        if (country != null)
        {
            System.out.println(
                    country.country_code + " "
                            + country.country_name + " "
                            + country.continent + " "
                            + country.region + " "
                            + country.population + " "
                            + country.capitalName);
        }
        else
        {
            System.out.println("No country found");
        }
    }

    public City getCapitalCity(String name)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT city.Name, country.Name, city.Population "
                            + "FROM city JOIN country ON city.ID = country.Capital "
                            + "WHERE country.name LIKE '" + name + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new city if valid.
            // Check one is returned
            if (rset.next())
            {
                City capitalCity = new City();
                capitalCity.city_name = rset.getString("city.Name");
                capitalCity.countryName = rset.getString("country.Name");
                capitalCity.population = rset.getInt("city.Population");

                return capitalCity;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get capital city details");
            return null;
        }
    }

    public void displayCapitalCity(City capitalCity)
    {
        if (capitalCity != null)
        {
            System.out.println(
                    capitalCity.city_name + " "
                            + capitalCity.countryName + " "
                            + capitalCity.population);
        }
        else
        {
            System.out.println("No capital city found");
        }
    }



    public Country getCountryPopulation(String name)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT country.Name, country.Population, SUM(city.Population), (SUM(city.Population)/country.Population)*100, country.Population-SUM(city.Population), ((country.Population-SUM(city.Population))/country.Population)*100 "
                            + "FROM country JOIN city ON country.Code = city.CountryCode "
                            + "WHERE country.Name LIKE '" + name + "' "
                            + "GROUP BY country.Name, country.Population";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new country for population if valid.
            // Check one is returned
            if (rset.next())
            {
                Country countryPop = new Country();
                countryPop.country_name = rset.getString("country.Name");
                countryPop.population = rset.getInt("country.Population");
                countryPop.allCityPopulation = rset.getInt("SUM(city.Population)");
                countryPop.allCityPopulationPercentage = rset.getFloat("(SUM(city.Population)/country.Population)*100");
                countryPop.notCityPopulation = rset.getInt("country.Population-SUM(city.Population)");
                countryPop.notCityPopulationPercentage = rset.getFloat("((country.Population-SUM(city.Population))/country.Population)*100");

                return countryPop;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get population details");
            return null;
        }
    }

    public void displayCountryPopulation(Country countryPop)
    {
        if (countryPop != null)
        {
            System.out.println(
                    countryPop.country_name + " "
                            + countryPop.population + " "
                            + countryPop.allCityPopulation + " "
                            + countryPop.allCityPopulationPercentage + "% "
                            + countryPop.notCityPopulation + " "
                            + countryPop.notCityPopulationPercentage + "%");
        }
        else
        {
            System.out.println("No country population found");
        }
    }

    public Country getRegionPopulation(String region)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT country.Region, SUM(country.Population), SUM(city.Population), (SUM(city.Population)/country.Population)*100, country.Population-SUM(city.Population), ((country.Population-SUM(city.Population))/country.Population)*100 "
                            + "FROM country JOIN city ON country.Code = city.CountryCode "
                            + "WHERE country.Region LIKE '" + region + "' "
                            + "GROUP BY country.Region, country.Population";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new region for population if valid.
            // Check one is returned
            if (rset.next())
            {
                Country regionPop = new Country();
                regionPop.region = rset.getString("country.Region");
                regionPop.population = rset.getInt("SUM(country.Population)");
                regionPop.allCityPopulation = rset.getInt("SUM(city.Population)");
                regionPop.allCityPopulationPercentage = rset.getFloat("(SUM(city.Population)/country.Population)*100");
                regionPop.notCityPopulation = rset.getInt("country.Population-SUM(city.Population)");
                regionPop.notCityPopulationPercentage = rset.getFloat("((country.Population-SUM(city.Population))/country.Population)*100");

                return regionPop;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get population details");
            return null;
        }
    }

    public void displayRegionPopulation(Country regionPop)
    {
        if (regionPop != null)
        {
            System.out.println(
                    regionPop.region + " "
                            + regionPop.population + " "
                            + regionPop.allCityPopulation + " "
                            + regionPop.allCityPopulationPercentage + "% "
                            + regionPop.notCityPopulation + " "
                            + regionPop.notCityPopulationPercentage + "%");
        }
        else
        {
            System.out.println("No region population found");
        }
    }

    public Country getContinentPopulation(String continent)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT country.Continent, SUM(country.Population), SUM(city.Population), (SUM(city.Population)/country.Population)*100, country.Population-SUM(city.Population), ((country.Population-SUM(city.Population))/country.Population)*100 "
                            + "FROM country JOIN city ON country.Code = city.CountryCode "
                            + "WHERE country.Continent LIKE '" + continent + "' "
                            + "GROUP BY country.Continent, country.Population";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new region for population if valid.
            // Check one is returned
            if (rset.next())
            {
                Country continentPop = new Country();
                continentPop.continent = rset.getString("country.Continent");
                continentPop.population = rset.getInt("SUM(country.Population)");
                continentPop.allCityPopulation = rset.getInt("SUM(city.Population)");
                continentPop.allCityPopulationPercentage = rset.getFloat("(SUM(city.Population)/country.Population)*100");
                continentPop.notCityPopulation = rset.getInt("country.Population-SUM(city.Population)");
                continentPop.notCityPopulationPercentage = rset.getFloat("((country.Population-SUM(city.Population))/country.Population)*100");

                return continentPop;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get population details");
            return null;
        }
    }

    public void displayContinentPopulation(Country continentPop)
    {
        if (continentPop != null)
        {
            System.out.println(
                    continentPop.continent + " "
                            + continentPop.population + " "
                            + continentPop.allCityPopulation + " "
                            + continentPop.allCityPopulationPercentage + "% "
                            + continentPop.notCityPopulation + " "
                            + continentPop.notCityPopulationPercentage + "%");
        }
        else
        {
            System.out.println("No continent population found");
        }
    }


    public City getCityForCountry(int ID)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT ID, Name "
                            + "FROM city "
                            + "WHERE ID = " + ID;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new city if valid.
            // Check one is returned
            if (rset.next())
            {
                City city = new City();
                city.city_ID = rset.getInt("ID");
                city.city_name = rset.getString("Name");

                return city;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get city details");
            return null;
        }
    }

    public Country getCountryForCity(String code)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT Code, Name "
                            + "FROM country "
                            + "WHERE Code LIKE '" + code + "'";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new country if valid.
            // Check one is returned
            if (rset.next())
            {
                Country country = new Country();
                country.country_code = rset.getString("Code");
                country.country_name = rset.getString("Name");

                return country;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get country details");
            return null;
        }
    }











    /**
     * Gets all the current employees and salaries.
     * @return A list of all employees and salaries, or null if there is an error.
     */
   /* public ArrayList<Employee> getAllSalaries()
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Prints a list of employees.
     * @param employees The list of employees to print.
     */
   /* public void printSalaries(ArrayList<Employee> employees)
    {
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    } */

}