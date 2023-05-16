package com.peterjxl.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;

import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class SearchIndex {
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    @Before
    public void init() throws Exception {
        indexReader = DirectoryReader.open(FSDirectory.open(new File("D:\\temp\\index").toPath()));
        indexSearcher = new IndexSearcher(indexReader);
    }

    @Test
    public void testRangeQuery() throws Exception {
        // 创建一个Query对象
        Query query = LongPoint.newRangeQuery("size", 0L, 100L);

        // 执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);

        System.out.println("总记录数： " + topDocs.totalHits);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            System.out.println("文档id： " + docId);
            System.out.println("文档得分： " + scoreDoc.score);
            Document document = indexSearcher.doc(docId);
            System.out.println("name: " + document.get("name"));
            System.out.println("path: " + document.get("path"));
            System.out.println("size: " + document.get("size"));
            System.out.println("content: " + document.get("content"));
            System.out.println("-------------分割线-----------------");
        }

        indexReader.close();
    }

    @Test
    public void testQueryParser() throws Exception{
        // 创建一个QueryParser对象，需要两个参数，参数1：默认搜索域   参数2：分析器对象
        QueryParser queryParser = new QueryParser("name", new IKAnalyzer());

        Query query = queryParser.parse("lucene是一个Java开发的全文检索工具包");

        // 执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("总记录数： " + topDocs.totalHits);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            System.out.println("文档id： " + docId);
            System.out.println("文档得分： " + scoreDoc.score);
            Document document = indexSearcher.doc(docId);
            System.out.println("name: " + document.get("name"));
            System.out.println("path: " + document.get("path"));
            System.out.println("size: " + document.get("size"));
            System.out.println("-------------分割线-----------------");
        }

        indexReader.close();
    }
}
