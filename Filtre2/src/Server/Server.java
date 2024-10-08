package Server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
 private static ServerSocket listener;

 public static void main(String[] args) throws Exception {
     int clientNumber = 0;

     Scanner scanner = new Scanner(System.in);
     String serverAddress;
     int portDecoute;

     while (true) {
         System.out.println("Entrez l'adresse IP du poste sur lequel s'éxécute le serveur : ");
         serverAddress = scanner.next();

         if (isValidIPAddress(serverAddress)) {
             break;
         } else {
             System.out.println("Adresse IP invalide, veuillez réessayer : ");
         }
     }

     while (true) {
         System.out.println("Entrez le numéro du port d'écoute compris entre 5000 et 5050 : ");
         portDecoute = scanner.nextInt();
         scanner.close();

         if (5000 <= portDecoute && portDecoute <= 5050) {
             break;
         } else {
             System.out.println("Numéro de port invalide, veuillez réessayer : ");
         }
     }

     listener = new ServerSocket();
     listener.setReuseAddress(true);
     InetAddress serverIP = InetAddress.getByName(serverAddress);
     listener.bind(new InetSocketAddress(serverIP, portDecoute));
     System.out.format("The server is running on %s:%d%n", serverAddress, portDecoute);

     try {
         while (true) {
             new ClientHandler(listener.accept(), clientNumber++).start();
         }
     } finally {
         listener.close();
     }
 }

 public static boolean isValidIPAddress(String ipAddress) {
     String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
     Pattern pattern = Pattern.compile(ipRegex);
     Matcher matcher = pattern.matcher(ipAddress);
     return matcher.matches();
 }
}
