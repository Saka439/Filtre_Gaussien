package Server;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import util.GaussianBlur;


public class ClientHandler extends Thread {
    private Socket socket;
    private int clientNumber;
    private Map<String, String> userDatabase;

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.userDatabase = loadUserDatabase(); 
        System.out.println("Nouvelle connexion avec le client# " + clientNumber + " à " + socket);
    }

    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

           
            String username = in.readUTF().trim();
            String password = in.readUTF().trim();
            System.out.println("Reçu - Nom d'utilisateur: " + username + ", Mot de passe: " + password);

          
            if (authenticateOrCreateUser(username, password)) {
                out.writeUTF("Authentification réussie!");
                System.out.println("Authentification réussie pour le client# " + clientNumber);

               
                BufferedImage inputImage = receiveImage(in);
                System.out.println("Image reçue pour le client# " + clientNumber);

             
                BufferedImage outputImage = GaussianBlur.applyGaussianBlur(inputImage);
                System.out.println("Filtre de flou Gaussien appliqué pour le client# " + clientNumber);

               
                sendImage(out, outputImage);
                System.out.println("Image traitée envoyée au client# " + clientNumber);
            } else {
                out.writeUTF("Échec de l'authentification.");
                System.out.println("Échec de l'authentification pour le client# " + clientNumber);
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Erreur de manipulation avec le client# " + clientNumber + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean authenticateOrCreateUser(String username, String password) {
        
        if (userDatabase.containsKey(username)) {
            
            return userDatabase.get(username).equals(password);
        } else {
           
            userDatabase.put(username, password);
            saveUserDatabase(); 
            return true;
        }
    }

    private Map<String, String> loadUserDatabase() {
        Map<String, String> userDatabase = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("user_database.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                userDatabase.put(parts[0], parts[1]);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de la base de données des utilisateurs : " + e.getMessage());
        }
        return userDatabase;
    }

    private void saveUserDatabase() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("user_database.txt"));
            for (Map.Entry<String, String> entry : userDatabase.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Erreur lors de l'enregistrement de la base de données des utilisateurs : " + e.getMessage());
        }
    }

    private BufferedImage receiveImage(DataInputStream in) throws IOException {
        int width = in.readInt();
        int height = in.readInt();
        System.out.println("Dimensions de l'image reçue - Largeur: " + width + ", Hauteur: " + height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = in.readInt();
                image.setRGB(x, y, pixel);
            }
        }
        System.out.println("Image reçue avec succès.");
        return image;
    }

    private void sendImage(DataOutputStream out, BufferedImage image) throws IOException {
        out.writeInt(image.getWidth());
        out.writeInt(image.getHeight());
        System.out.println("Envoi de l'image - Largeur: " + image.getWidth() + ", Hauteur: " + image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                out.writeInt(image.getRGB(x, y));
            }
        }
        System.out.println("Image envoyée avec succès.");
    }
}
