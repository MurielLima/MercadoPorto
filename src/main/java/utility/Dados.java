/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author Muriel
 */
public class Dados {

    private BufferedReader br = null;
    private String nomeArq;
    private String Linha;
    

    public Dados(String nomeArq) {
        int i, j;
        this.nomeArq = nomeArq;
    }
    public String ler() {
        String linha = null;

        try {
            br = new BufferedReader(new FileReader(nomeArq));
            while ((linha = br.readLine()) != null) {
                Linha = linha;
            }
        } catch (Exception e) {
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
            }
        }

        return linha;
    }
   
}
