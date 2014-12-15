package org.ansj.knowledge;

import static org.ansj.util.MyStaticValue.LIBRARYLOG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.util.IOUtil;
import org.nlpcn.commons.lang.util.StringUtil;

public class KnowledgeExtraction {
	// 读取半机构化文本
	public static void loadFile(Forest forest, File file,File[] domainfile) {
		// TODO Auto-generated method stub
		if (!file.canRead()) {
			LIBRARYLOG.warning("file in path " + file.getAbsolutePath() + " can not to read!");
			return;
		}
		String temp = null;
		BufferedReader br = null;
		String[] strs = null;
		Value value = null;
		try {
			br = IOUtil.getReader(new FileInputStream(file), "UTF-8");
			ArrayList<String> notSeg=new ArrayList<String>();
			while ((temp = br.readLine()) != null) {
				if (StringUtil.isBlank(temp)) {
					continue;
				} else {
					System.out.println("读取词："+temp);
					List<Term> terms = ToAnalysis.parse(temp);
					System.out.println("增加新词例子:" + terms);
					String loc="",pos="",tmp="";
					for(int i=0;i<terms.size();i++){
						if(terms.get(i).getNatureStr().equals("location")){
							loc=terms.get(i).getName();
						}else if(terms.get(i).getNatureStr().equals("position")){
							pos=terms.get(i).getName();
						}else{
							tmp+=terms.get(i).getName();
						}
					}
					if(loc.equals("")&&pos.equals("")){
						//词未分出来
						notSeg.add(temp+"\r\n");
					}else if(!loc.equals("")&&!pos.equals("")){
						//领域词典中已有
						if(!tmp.equals("")){
							//需要纠正词典,将该词加入notSeg的list中去
							notSeg.add(temp+"\r\n");
						}
					}else if(loc.equals("")){
						//添加新词到position词典中
						notSeg.add(temp+"\r\n");
						loc=tmp;
						System.out.println("发现location新词"+loc);
						UserDefineLibrary.insertWord(loc, "location", 1000);
						System.out.println("写入文件"+domainfile[0].getAbsolutePath());
						domainwrite("\r\n"+loc+"\tlocation\t1000",domainfile[0].getAbsolutePath(),true);
					}else if(pos.equals("")){
						//添加新词到location词典中,可能仍需纠正，加入到未分词中
						notSeg.add(temp+"\r\n");
						pos=tmp;
						System.out.println("发现position新词"+pos);
						UserDefineLibrary.insertWord(pos, "position", 1000);
						System.out.println("写入文件"+domainfile[1].getAbsolutePath());
						domainwrite("\r\n"+pos+"\tposition\t1000\r\n",domainfile[1].getAbsolutePath(),true);
					}
				}
			}
			notSegwrite(notSeg,file);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUtil.close(br);
			br = null;
		}
	}
	
    private static void notSegwrite(
			ArrayList<String> notSeg,
			File file) {
		// TODO Auto-generated method stub
    	domainwrite("",file.getAbsolutePath(),false);
    	for(int i=0;i<notSeg.size();i++){
    		domainwrite(notSeg.get(i),file.getAbsolutePath(),true);
    	}
		
	}
	public static void domainwrite(String content,String fileName,boolean appendOrNot) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, appendOrNot);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
