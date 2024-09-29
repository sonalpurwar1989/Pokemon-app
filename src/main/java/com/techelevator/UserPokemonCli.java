package com.techelevator;

import com.techelevator.dao.JdbcPokemonDao;
import com.techelevator.dao.JdbcUserDao;
import com.techelevator.dao.PokemonDao;
import com.techelevator.dao.UserDao;
import com.techelevator.models.Pokemon;
import com.techelevator.models.PokemonDetail;
import com.techelevator.models.User;
import com.techelevator.security.PasswordHasher;
import com.techelevator.services.PokemonService;
import org.apache.commons.dbcp2.BasicDataSource;
import org.bouncycastle.util.encoders.Base64;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserPokemonCli {

    private PokemonService service = new PokemonService();
    private Scanner input;
    private PasswordHasher passwordHasher;

    private UserDao userDao;

    private PokemonDao pokemonDao;

    private User loggedInUser;

    public UserPokemonCli(DataSource datasource){
        passwordHasher = new PasswordHasher();
        userDao = new JdbcUserDao(datasource);
        pokemonDao = new JdbcPokemonDao(datasource);
        input = new Scanner(System.in);
    }

    public static void main(String[] args) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/pokemon_db");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        UserPokemonCli app = new UserPokemonCli(dataSource);
        app.run();
    }

    public void run(){
        printGreeting();

        while (true) {
            printMenu();
            String option = askPrompt();

            if ("a".equalsIgnoreCase(option)) {
                addNewUser();
            } else if ("s".equalsIgnoreCase(option)) {
                showUsers();
            } else if ("l".equalsIgnoreCase(option)) {
                loginUser();
            } else if ("p".equalsIgnoreCase(option)){
                pokemonMenu();
            } else if ("q".equalsIgnoreCase(option)) {
                System.out.println("Thanks for using the User Pokemon App!");
                break;
            } else {
                System.out.println(option + " is not a valid option. Please select again.");
            }
        }
    }
    public void pokemonMenu(){

        if (loggedInUser == null) {
            System.out.println("Sorry. Only logged in users can see other users.");
            System.out.println("Press enter to continue...");
            System.out.flush();
            input.nextLine();
            return;
        }
        List<Pokemon> pokemon = null;

        do {
            System.out.println("Let's catch some Pokemon!");
            System.out.println("1. get first group of 20");
            System.out.println("2. get second group of 20");
            System.out.println("3. get third group of 20");
            System.out.println("4. return to main menu");

            System.out.print("\n Enter your choice: ");
            int choice = Integer.parseInt(input.nextLine());
            switch (choice) {
                case 1:
                    pokemon = service.getMorePokemon(0);
//                    System.out.println(pokemon);
                    break;
                case 2:
                    pokemon = service.getMorePokemon(20);
//                    System.out.println(pokemon);
                    break;
                case 3:
                    pokemon = service.getMorePokemon(40);
//                    System.out.println(pokemon);
                    break;
            }
            if (choice == 4){
                break;
            }
            printDetail(pokemon);

        } while (true);

    }

    public void printDetail(List<Pokemon> list){
        System.out.println("Choose pokemon:");
        for (Pokemon p : list) {
            System.out.println(p.getId() + "  " + p.getName());
        }
        System.out.println("Enter choice: ");
        int id = Integer.parseInt(input.nextLine());
        PokemonDetail detail = service.getPokemonDetailById(id);
        System.out.println(detail);
        System.out.println();
        System.out.print("Save to Favorites? (y/n): ");
        String choice = input.nextLine();
        if (choice.equalsIgnoreCase("y")){
            // we need to save to the database
            pokemonDao.saveFavorites(detail, loggedInUser.getUserId());
        }

    }

    /**
     * Take a username and password from the user and check it against
     * the DAO via the isUsernameAndPasswordValid() method.
     * If the method returns false it means the username/password aren't valid.
     * You don't know what's specifically wrong about the login, just that the combined
     * username & password aren't valid. You don't want to give an attacker any information about
     * what they got right or what they got wrong when trying this. Information
     * like that is gold to an attacker because then they know what they're
     * getting right and what they're getting wrong.
     */
    private void loginUser() {
        System.out.println("Log into the system");
        System.out.print("Username: ");
        System.out.flush();
        String username = input.nextLine();
        System.out.print("Password: ");
        System.out.flush();
        String password = input.nextLine();

        if (isUsernameAndPasswordValid(username, password)) {
            loggedInUser = new User();
            loggedInUser.setUsername(username);
            User user = userDao.getUserByUsername(username);
            loggedInUser.setUserId(user.getUserId());
            System.out.println("Welcome " + username + "!");
            System.out.println();
        } else {
            System.out.println("That login is not valid, please try again.");
            System.out.println();
        }
    }

    /**
     * Check the username and password are valid.
     *
     * @param username the supplied username to validate
     * @param password the supplied password to validate
     * @return true is username and password are valid and correct
     */
    private boolean isUsernameAndPasswordValid(String username, String password) {
        Map<String, String> passwordAndSalt = userDao.getPasswordAndSaltByUsername(username);
        if (passwordAndSalt.isEmpty()){
            return false;
        }
        String storedSalt = passwordAndSalt.get("salt");
        String storedPassword = passwordAndSalt.get("password");
        String hashedPassword = passwordHasher.computeHash(password, Base64.decode(storedSalt));
        return storedPassword.equals(hashedPassword);
    }

    /**
     * Add a new user to the system. Anyone can register a new account like
     * this. It calls createUser() in the DAO to save it to the data store.
     */
    private void addNewUser() {
        System.out.println("Enter the following information for a new user: ");
        System.out.print("Username: ");
        System.out.flush();
        String username = input.nextLine();
        System.out.print("Password: ");
        System.out.flush();
        String password = input.nextLine();

        // generate hashed password and salt
        byte[] salt = passwordHasher.generateRandomSalt();
        String hashedPassword = passwordHasher.computeHash(password, salt);
        String saltString = new String(Base64.encode(salt));

        User user = userDao.createUser(username, hashedPassword, saltString);
        System.out.println("User " + user.getUsername() + " added with ID " + user.getUserId() + "!");
        System.out.println();
    }

    /**
     * Show all the users that are in the data store. You can't show passwords
     * because you don't have them! Passwords in the data store are hashed and
     * you can see that by opening up a SQL client like pgAdmin or DbVisualizer
     * and looking at what's stored in the `users` table.
     *
     * Only allow access to this to logged-in users. If a user isn't logged
     * in, give them a message and leave. Having an `if` statement like this
     * at the top of the method is a common way of handling authorization checks.
     */
    private void showUsers() {
        if (loggedInUser == null) {
            System.out.println("Sorry. Only logged in users can see other users.");
            System.out.println("Press enter to continue...");
            System.out.flush();
            input.nextLine();
            return;
        }

        List<User> users = userDao.getUsers();
        System.out.println("Users currently in the system are: ");
        for (User user : users) {
            System.out.println(user.getUserId() + ". " + user.getUsername());
        }
        System.out.println();
        System.out.println("Press enter to continue...");
        System.out.flush();
        input.nextLine();
        System.out.println();
    }

    private void printMenu() {
        System.out.println("(A)dd a new User");
        System.out.println("(S)how all users");
        System.out.println("(L)og in");
        System.out.println("(P)Show Pokemon");
        System.out.println("(Q)uit");
        System.out.println();
    }

    private String askPrompt() {
        String name;
        if (loggedInUser == null) {
            name = "Unauthenticated User";
        } else {
            name = loggedInUser.getUsername();
        }

        System.out.println("Welcome " + name + "!");
        System.out.print("What would you like to do today? ");
        System.out.flush();
        String selection;
        try {
            selection = input.nextLine();
        } catch (Exception ex) {
            selection = "*";
        }
        return selection;
    }

    private void printGreeting() {
        System.out.println("Welcome to the User Pokemon Application!");
        System.out.println();
    }
}