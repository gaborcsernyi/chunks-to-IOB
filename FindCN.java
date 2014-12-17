import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;


public class FindCN {
	public static void main(String[] args) {
				
		try{
			FileInputStream fis = new FileInputStream("input_file");
			BufferedReader raw = new BufferedReader (new InputStreamReader (fis,"UTF-8"));
			FileOutputStream out1 = new FileOutputStream("output_file");
		    OutputStreamWriter tagfile = new OutputStreamWriter(out1,"UTF8");
		    
		    String s = "";
	    		    	
		    ArrayList<String> tags = new ArrayList<String>();
		    ArrayList<String> all = new ArrayList<String>();
		    ArrayList<String> na = new ArrayList<String>();
		    ArrayList<String> nv = new ArrayList<String>();
		    ArrayList<String> dob = new ArrayList<String>();
		    ArrayList<String> pn = new ArrayList<String>();
		    ArrayList<String> sub = new ArrayList<String>();
		    ArrayList<String> pob = new ArrayList<String>();
		    
		    String xline = "";
		    String rline = "";
		    String pline = "";
		    int scount = 0;
		    int cnt = 0;
		    int ab = 0;
		    int bc = 0;
		    ArrayList<String> collcurrncs = new ArrayList<String>();
		    
		    while ( (s = raw.readLine()) != null ){
		    	
		    	if (cnt == 0){
		    		rline = s;
		    		String[] tokens;
		    		tokens = s.split(" ");
		    		ab = tokens.length;
		    		//System.out.println(rline);
		    	}
		    	
		    	else if (cnt == 1){
		    		//if (s.indexOf("<cn rel=\"PN") != -1)
		    		//if (s.indexOf("<cn rel=\"NA") != -1 || s.indexOf("<cn rel=\"NV") != -1 || s.indexOf("<cn rel=\"SUB") != -1 || s.indexOf("<cn rel=\"POB") != -1 || s.indexOf("<cn rel=\"DOB") != -1)
		    		if (s.indexOf("<cn rel=\"PN") != -1 || s.indexOf("<cn rel=\"NA") != -1 || s.indexOf("<cn rel=\"NV") != -1 || s.indexOf("<cn rel=\"SUB") != -1 || s.indexOf("<cn rel=\"POB") != -1 || s.indexOf("<cn rel=\"DOB") != -1)
		    			scount++;
		    		else { //only those sentences that contain CNs
		    			cnt = -1;
			    		collcurrncs.clear();
			    		continue;
		    		}
		    		xline = s;
		    		//System.out.println(xline);
		    		String tmp = "";
			    	String tmp2 = "";
			    	String type = "";
			    	int mempos = -1;
		    		while (s.indexOf("<cn rel=") != -1){
			    		tmp = s.substring(s.indexOf("<cn rel=")+9, s.indexOf("\" ", s.indexOf("<cn rel=")+9));
			    		if (!tags.contains(tmp))
			    			tags.add(tmp);
			    		int opentagend = s.indexOf("\">", s.indexOf("<cn rel="))+2;
			    		int closetagstart = s.indexOf("</cn>", opentagend);
			    		type = s.substring(s.indexOf("rel=\"")+5, s.indexOf("\"", s.indexOf("rel=\"")+5)); 
			    		tmp2 = s.substring(opentagend, closetagstart);
			    		int posi = rline.indexOf(tmp2, mempos); 
			    		//if (type.equals("NA") || type.equals("NV") || type.equals("SUB") || type.equals("DOB") || type.equals("POB")){
			    		//if (type.equals("PN")){
			    		if (type.equals("PN") || type.equals("NA") || type.equals("NV") || type.equals("SUB") || type.equals("DOB") || type.equals("POB")){
			    			//System.out.println(tmp2 + "\t" + type);
				    		String[] korte = tmp2.split(" ");
				    		for (int q=0; q<korte.length; ++q){
				    			if (q == 0) {
				    				collcurrncs.add(korte[q] + " " + "B-NP" + " " + posi); 
				    				//System.out.println("\n### " + korte[q] + " " + "B-NP" + " " + posi);
				    			}
				    			else {
				    				collcurrncs.add(korte[q] + " " + "I-NP" + " " + rline.indexOf(korte[q], posi));
				    				//System.out.println("### " + korte[q] + " " + "I-NP" + " " + rline.indexOf(korte[q], posi));
				    			}
				    		}
			    		}
			    		mempos = posi;
			    		
			    		//System.out.println(rline.indexOf(tmp2));
			    		
			    		if (type.equals("NV"))
			    			nv.add(tmp2);
			    		else if (type.equals("NA"))
			    			na.add(tmp2);
			    		else if (type.equals("PN"))
			    			pn.add(tmp2);
			    		else if (type.equals("SUB"))
			    			sub.add(tmp2);
			    		else if (type.equals("POB"))
			    			pob.add(tmp2);
			    		else if (type.equals("DOB"))
			    			dob.add(tmp2);
			    		
			    		all.add(tmp2);
			    		s = s.substring(closetagstart+4);
			    	}	    		
		    	}
		    	
		    	else if (cnt == 2){
		    		pline = s;
				    String[] word_pos;
				    String tmp_tosplit = s.substring(2,s.length()-2);
			    	word_pos = tmp_tosplit.split("\\), \\(");
			    	bc = word_pos.length;
		    		if (ab != bc) System.out.println(" ### ERROR! ### " + rline + "\n" + pline + "\n");
		    		
		    		int status = 0;
			    	String cprline = rline;
			    	for (int wpc=0; wpc<word_pos.length; ++wpc){
			    		status = 0;
			    		String[] wp_sep;
			    		wp_sep = word_pos[wpc].split(", ");
			    		String wform = wp_sep[0].substring(1, wp_sep[0].length()-1);
			    		String wpos = wp_sep[1].substring(1, wp_sep[1].length()-1);
			    		cprline = cprline.substring(cprline.indexOf(wform));
			    		//System.out.println(cprline);
			    		for (int r=0; r<collcurrncs.size(); ++r){
			    			String gs = collcurrncs.get(r);
			    			String[] gssp = gs.split(" ");
			    			//System.out.println(Integer.parseInt(gssp[2]) + "\t" + rline.indexOf(wform,lastp));
			    			if ((Integer.parseInt(gssp[2]) == rline.indexOf(cprline))){
			    				//System.out.println(wform + "\t" + wpos + "\t" + gssp[1]);
			    				tagfile.write(wform + "\t" + wpos + "\t" + gssp[1] + "\n");
			    				//System.out.println(lastp);
			    				status = 1;
			    			}
			    		}
			    		if (status == 1) continue;
			    		
			    		//System.out.println(wform + "\t" + wpos + "\t" + "O");
			    		tagfile.write(wform + "\t" + wpos + "\t" + "O" + "\n");
			    	}
			    	tagfile.write("\n");
		    		//System.out.println(pline);
		    		cnt = -1;
		    		collcurrncs.clear();
		    	}
		    	++cnt;		    	
		    }
		    System.out.println(scount);
		    
		    for (int x=0; x<tags.size(); x++){
		    	System.out.println(tags.get(x));
		    }
		    System.out.println("NA: " + na.size() + " NV: " + nv.size() + " PN: " + pn.size() + " SUB: " + sub.size() + " POB: " + pob.size() + " DOB: " + dob.size());
		    		    
		    raw.close();
		    tagfile.close();
		}
			
			

		catch (FileNotFoundException exc){
			System.err.println("File could not be found");
			System.exit(1);
		}
			    
		catch (IOException exc){
			System.err.println("I/O error");
			exc.printStackTrace();
			System.exit(2);
		}
			    
	}	
}
