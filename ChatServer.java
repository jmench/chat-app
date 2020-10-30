import java.net.*;
import java.util.*;

public class ChatServer {
  private int port;
  private Set<String> userNames = new HashSet<>();
  private Set<UserThread> userThreads = new HashSet<>();

  public ChatServer(int port) {
    this.port = port;
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Syntax: java ChatServer <portnum>");
      System.exit(0);
    }
    int port = Integer.parseInt(args[0]);

    ChatServer server = new ChatServer(port);
    server.execute();
  }

  private void execute() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server is up and listening on port " + port);

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("New user connected");

        UserThread newUser = new UserThread(socket, this);
        userThreads.add(newUser);
        newUser.start();
      }

    } catch (Exception e) {
      System.out.println("Error with server: " + e.getMessage());
      e.printStackTrace();
    }
  }

  void broadcast(String message, UserThread excludeUser) {
    for (UserThread aUser : userThreads) {
        if (aUser != excludeUser) {
            aUser.sendMessage(message);
        }
    }
}

  void addUserName(String userName) {
    userNames.add(userName);
  }

  void removeUser(String userName, UserThread aUser) {
    boolean removed = userNames.remove(userName);
    if (removed) {
      userThreads.remove(aUser);
      System.out.println("The user " + userName + " quitted");
    }
  }

  Set<String> getUserNames() {
    return this.userNames;
}

/**
 * Returns true if there are other users connected (not count the currently connected user)
 */
boolean hasUsers() {
    return !this.userNames.isEmpty();
}
}
