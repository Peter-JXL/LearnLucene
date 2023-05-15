package com.peterjxl.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;


/**
 * 演示不同的Field
 */
public class LuceneSecond {
    @Test
    public void createIndex() throws Exception {
        //  1. 创建一个Director对象，指定索引库保存的位置。
        Directory directory = FSDirectory.open(new File("D:\\temp\\index").toPath());

        //  2. 基于Directory对象创建一个IndexWriter对象（用来写索引）
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig());

        //  3. 读取磁盘上的文件，对应每个文件创建一个文档对象。
        File dir = new File("D:\\temp\\searchsource");
        File[] files = dir.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            String filePath = file.getPath();
            String fileContent = FileUtils.readFileToString(file, "utf-8");
            long fileSize = FileUtils.sizeOf(file);

            // 4. 创建Field
            // 参数1：域的名称, 参数2：域的内容, 参数3：是否存储
            Field fieldName = new TextField("name", fileName, Field.Store.YES);
            Field fieldPath = new StoredField("path", filePath);
            Field fieldContent = new TextField("content", fileContent, Field.Store.YES);
            // Field fieldSize = new TextField("size", String.valueOf(fileSize), Field.Store.YES);
            Field fieldSize = new LongPoint("size", fileSize);
            Field fieldSizeStore = new StoredField("size", fileSize);

            // 4. 将field添加到document对象中。
            Document document = new Document();
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContent);
            document.add(fieldSize);
            document.add(fieldSizeStore);

            //  5. 使用IndexWriter对象将document对象写入索引库，此过程进行索引创建，并将索引和document对象写入索引库。
            indexWriter.addDocument(document);
        }

        //  6. 关闭IndexWriter对象。
        indexWriter.close();
    }

}
