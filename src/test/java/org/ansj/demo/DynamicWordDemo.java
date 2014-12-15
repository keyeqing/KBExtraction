package org.ansj.demo;

import java.io.File;


import org.ansj.knowledge.KnowledgeExtraction;
import org.ansj.library.UserDefineLibrary;
import org.nlpcn.commons.lang.tire.domain.Forest;

/**
 * 动态添加删除用户自定义词典!
 * 
 * @author ansj
 * 
 */
public class DynamicWordDemo {
	public static void main(String[] args) {
		// 增加新词,中间按照'\t'隔开
		Forest forest=UserDefineLibrary.FOREST;
		UserDefineLibrary.loadLibrary(forest, "library/domainlib");
		//UserDefineLibrary.insertWord("清华大学", "location", 1000);
/*		List<Term> terms = ToAnalysis.parse("清华大学教授");
		System.out.println("增加新词例子:" + terms);*/
		File domainfile = new File("library/domainlib");
		File[] domainfiles = domainfile.listFiles();
		File file=new File("library/unstructured.dic");
		KnowledgeExtraction.loadFile(forest, file,domainfiles);
	}
}
