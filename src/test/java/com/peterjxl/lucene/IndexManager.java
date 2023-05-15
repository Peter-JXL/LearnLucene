package com.peterjxl.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class IndexManager {


    @Test
    public void addDocument() throws Exception {
        // 创建一个IndexWriter对象，需要使用IKAnalyzer作为分析器
        Directory directory = FSDirectory.open(new File("D:\\temp\\index").toPath());
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new IKAnalyzer()));

        // 创建一个Document对象
        Document document = new Document();

        // 向document对象中添加域，不同的document可以有不同的域，同一个document可以有相同的域。
        document.add(new TextField("name", "新添加的文档", Field.Store.YES));
        document.add(new TextField("content", "新添加的文档的内容", Field.Store.NO));
        document.add(new StoredField("path", "d:/temp/1.txt"));

        // 把文档对象写入索引库
        indexWriter.addDocument(document);

        // 关闭IndexWriter对象
        indexWriter.close();
    }

    //删除全部索引
    @Test
    public void deleteAllDocument() throws Exception {
        IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File("D:\\temp\\index").toPath()), new IndexWriterConfig(new IKAnalyzer()));
        indexWriter.deleteAll();
        indexWriter.close();
    }

    //根据查询条件删除索引
    @Test
    public void deleteDocumentByQuery() throws Exception {
        IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File("D:\\temp\\index").toPath()), new IndexWriterConfig(new IKAnalyzer()));

        // 删除文件名中包含apache的文档
        indexWriter.deleteDocuments(new Term("name", "apache"));
        indexWriter.close();
    }

    //修改索引库
    @Test
    public void updateDocument() throws Exception {
        IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File("D:\\temp\\index").toPath()), new IndexWriterConfig(new IKAnalyzer()));

        Document document = new Document();
        document.add(new TextField("name", "更新之后的文档", Field.Store.YES));
        document.add(new TextField("name1", "更新之后的文档1", Field.Store.YES));
        document.add(new TextField("name2", "更新之后的文档2", Field.Store.YES));
        indexWriter.updateDocument(new Term("name", "spring"), document);

        indexWriter.close();
    }
}
