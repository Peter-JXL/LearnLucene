package com.peterjxl.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;

public class LuceneFirst {


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
            Field fieldPath = new TextField("path", filePath, Field.Store.YES);
            Field fieldContent = new TextField("content", fileContent, Field.Store.YES);
            Field fieldSize = new TextField("size", String.valueOf(fileSize), Field.Store.YES);

            // 4. 将field添加到document对象中。
            Document document = new Document();
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContent);
            document.add(fieldSize);

            //  5. 使用IndexWriter对象将document对象写入索引库，此过程进行索引创建，并将索引和document对象写入索引库。
            indexWriter.addDocument(document);
        }

        //  6. 关闭IndexWriter对象。
        indexWriter.close();
    }

    @Test
    public void searchIndex() throws Exception {
        // 1. 第一步：创建一个Directory对象，也就是索引库存放的位置。
        Directory directory = FSDirectory.open(new File("D:\\temp\\index").toPath());

        // 2. 第二步：创建一个indexReader对象，需要指定Directory对象。
        IndexReader indexReader = DirectoryReader.open(directory);

        // 3. 第三步：创建一个indexsearcher对象，需要指定IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // 4. 第四步：创建一个TermQuery对象，指定查询的域和查询的关键词。
        Query query = new TermQuery(new Term("content", "spring"));


        // 5. 第五步：执行查询。得到一个TopDocs对象，包含查询结果的总记录数，和文档列表
        // 参数1：查询对象 参数2：查询结果返回的最大记录数
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("查询出来的总记录数：" + topDocs.totalHits);

        // 6. 第六步：返回查询结果。遍历查询结果并输出。
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            // 取文档id
            int docId = scoreDoc.doc;
            // 根据id取文档对象
            Document document = indexSearcher.doc(docId);
            // 取文档的属性
            System.out.println("name: " + document.get("name"));
            System.out.println("path: " + document.get("path"));
            System.out.println("size: " + document.get("size"));
            //System.out.println("content: " + document.get("content"));
            System.out.println("-------------分割线-----------------");
        }

        // 7. 第七步：关闭IndexReader对象
        indexReader.close();
    }
}
