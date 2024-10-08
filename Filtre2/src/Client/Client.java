package Client;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class Client {
    private static Socket socket;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String serverAddress;
        int portDecoute;
        String nomDutilisateur;
        String motDePasse;

        while (true) {
            System.out.println("Entrez l'adresse IP du poste sur lequel s'exécute le serveur : ");
            serverAddress = scanner.next();
            if (isValidIPAddress(serverAddress)) {
                break;
            } else {
                System.out.println("Adresse IP invalide, veuillez réessayer.");
            }
        }

        while (true) {
            System.out.println("Entrez le numéro du port d'écoute compris entre 5000 et 5050 : ");
            portDecoute = scanner.nextInt();
            if (5000 <= portDecoute && portDecoute <= 5050) {
                break;
            } else {
                System.out.println("Numéro de port invalide, veuillez réessayer.");
            }
        }

        while (true) {
            System.out.println("Entrez votre nom d'utilisateur : ");
            nomDutilisateur = scanner.next();
            if (isValidUsername(nomDutilisateur)) {
                break;
            } else {
                System.out.println("Nom d'utilisateur invalide, veuillez réessayer.");
            }
        }

        System.out.println("Entrez votre mot de passe : ");
        motDePasse = scanner.next();

        System.out.println("Entrez le chemin de l'image à traiter : ");
        String imagePath = scanner.next();

        try {
            socket = new Socket(serverAddress, portDecoute);
            System.out.format("Connecté au serveur [%s:%d]%n", serverAddress, portDecoute);

            
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

           
            out.writeUTF(nomDutilisateur);
            out.writeUTF(motDePasse);

            
            String response = in.readUTF();
            System.out.println(response);

           
            if (response.equals("Authentification réussie!")) {
               
                BufferedImage image = ImageIO.read(new File(imagePath));

             
                sendImage(out, image);
                System.out.println("Image envoyée pour le traitement.");

                
                BufferedImage processedImage = receiveImage(in);
                System.out.println("Image traitée reçue.");

               
                System.out.println("Entrez le chemin où vous souhaitez enregistrer l'image traitée : ");
                String processedImagePath = scanner.next();
                ImageIO.write(processedImage, "png", new File(processedImagePath));
                System.out.println("Image traitée sauvegardée.");
            }

          
            scanner.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }

    private static void sendImage(DataOutputStream out, BufferedImage image) throws IOException {
        out.writeInt(image.getWidth());
        out.writeInt(image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                out.writeInt(image.getRGB(x, y));
            }
        }
    }

    private static BufferedImage receiveImage(DataInputStream in) throws IOException {
        int width = in.readInt();
        int height = in.readInt();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = in.readInt();
                image.setRGB(x, y, pixel);
            }
        }
        return image;
    }

    public static boolean isValidIPAddress(String ipAddress) {
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(ipRegex);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }
}
