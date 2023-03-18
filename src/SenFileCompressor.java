import java.io.*;
import java.util.zip.*;
import java.util.*;
import picocli.CommandLine;
import picocli.CommandLine.*;

@Command(name = "Logiciel d'archivage et de compression de fichiers", mixinStandardHelpOptions = true, version = "SenFileCompressor V1.0", description = "\nOptions d'utilisation du programme :\n")
public class SenFileCompressor implements Runnable {
    @ArgGroup(exclusive = true, multiplicity = "1")
    Exclusive exclusive; 

    // exclusion mutuelle des options --compress et --decompress
    static class Exclusive {
        @Option(names = {"-c", "--compress"}, required = false, split = " ", description = "Cette option permet de compresser des fichiers donnes en parametre")
        String [] compress;
        @Option(names = {"-d", "--decompress"}, required = false, description = "Cette option permet de decompresser une archive donnee en parametre")
        String decompress;
    }
    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new SenFileCompressor()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        if ((exclusive.compress) != null) {
            String [] fichier = exclusive.compress; // Récupération des fichiers donnés en paramètre à l'option -c
            try 
            {
                List<String> srcFiles = new ArrayList<String>(); // Création d'une liste dynamique pour stocker les fichiers à compresser
                for (int i=0; i<fichier.length; i++) {
                    srcFiles.add(fichier[i]);                    // Ajout de fichier dans la liste à chaque tour de boucle
                }

                FileOutputStream fos = new FileOutputStream("D:\\PROJET_JAVA_DIT2\\CompressionFichiers\\src\\archive.sfc");
                ZipOutputStream zipOut = new ZipOutputStream(fos);

                for (String srcFile : srcFiles) {
                    File fileToZip = new File(srcFile);
                    FileInputStream fis = new FileInputStream(fileToZip);
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fis.close();
                }
                zipOut.close();
                fos.close();
                System.out.println("\nL'archive a bien été créée !");
            }
            catch(UnmatchedArgumentException e) {
                System.out.println("\nOups, erreur d'arguments !");
                e.getUnmatched();
            }
            catch(IOException e) {
                System.out.println("\nOups, erreur d'entrée ou de sortie !");
                e.printStackTrace();
            }
        }
        else if ((exclusive.decompress) != null) { 
            
            String zipFilePath = exclusive.decompress;  // Récupération du fichier à décompresser
            String destDir = "D:\\PROJET_JAVA_DIT2\\CompressionFichiers\\dossierDecompression"; // dossier de destination

            File dir = new File(destDir);   // création du répertoire au cas où il n'existe pas
            if(!dir.exists()) dir.mkdirs();
            FileInputStream fis;
            
            byte[] buffer = new byte[1024];     //buffer pour lire les données du flux entrant et les écrire dans le flux sortant
            try {
                fis = new FileInputStream(zipFilePath);
                ZipInputStream zis = new ZipInputStream(fis);
                ZipEntry ze = zis.getNextEntry();
                while(ze != null){
                    String fileName = ze.getName();
                    File newFile = new File(destDir + File.separator + fileName);
                    System.out.println("\nDécompression du fichier vers " + newFile.getAbsolutePath());
                    
                    new File(newFile.getParent()).mkdirs();    //création des sous répertoires
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    zis.closeEntry();
                    ze = zis.getNextEntry();
                }
                zis.closeEntry();
                zis.close();
                fis.close();
            }
            catch (IOException e) {
                System.out.println("\nFichier introuvable ! Voir la trace ci-dessous\n");
                e.printStackTrace();
            }
        }
    }
}