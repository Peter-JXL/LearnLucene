package com.peterjxl.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

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
        Query query = new TermQuery(new Term("name", "spring"));


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

    @Test
    public void testTokenSteam() throws Exception {
        // 1. 创建一个Analyzer对象，这里我们使用StandardAnalyzer对象
        Analyzer analyzer = new StandardAnalyzer();

        // 2. 使用分析器对象的tokenStream方法获得一个TokenStream对象
//        TokenStream tokenStream = analyzer.tokenStream("", "The Spring Framework provides a comprehensive programming and configuration model.");
        TokenStream tokenStream = analyzer.tokenStream("", "鲁镇的酒店的格局，是和别处不同的：都是当街一个曲尺形的大柜台，柜里面预备着热水，可以随时温酒。做工的人，傍午傍晚散了工，每每花四文铜钱，买一碗酒，——这是二十多年前的事，现在每碗要涨到十文，——靠柜外站着，热热的喝了休息；倘肯多花一文，便可以买一碟盐煮笋，或者茴香豆，做下酒物了，如果出到十几文，那就能买一样荤菜，但这些顾客，多是短衣帮⑴，大抵没有这样阔绰⑵。只有穿长衫的，才踱进店面隔壁的房子里，要酒要菜，慢慢地坐喝。");

        // 3. 向TokenStream对象中设置一个引用，相当于是一个指针。TokenStream对象包含了所有关键词，类似链表一样排列着，我们用一个指针指向关键词，访问指针就相当于访问关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        // 4. 调用TokenStream对象的rest方法，用处是将指针指向第一个数据（如果不调用会抛异常）
        tokenStream.reset();

        // 5. 使用while循环遍历TokenStream对象
        while (tokenStream.incrementToken()) {  //返回true说明有下一个元素
            System.out.println(charTermAttribute.toString());
        }

        // 6. 关闭TokenStream
        tokenStream.close();
    }


    @Test
    public void testIKAnalyzer() throws Exception{
        // 1. 创建一个Analyzer对象，这里我们使用StandardAnalyzer对象
        Analyzer analyzer = new IKAnalyzer();

        // 2. 使用分析器对象的tokenStream方法获得一个TokenStream对象
        TokenStream tokenStream = analyzer.tokenStream("", "鸡你太美 鲁镇的酒店的格局，是和别处不同的：都是当街一个曲尺形的大柜台，柜里面预备着热水，可以随时温酒。做工的人，傍午傍晚散了工，每每花四文铜钱，买一碗酒，——这是二十多年前的事，现在每碗要涨到十文，——靠柜外站着，热热的喝了休息；倘肯多花一文，便可以买一碟盐煮笋，或者茴香豆，做下酒物了，如果出到十几文，那就能买一样荤菜，但这些顾客，多是短衣帮⑴，大抵没有这样阔绰⑵。只有穿长衫的，才踱进店面隔壁的房子里，要酒要菜，慢慢地坐喝。");

        // 3. 向TokenStream对象中设置一个引用，相当于是一个指针。TokenStream对象包含了所有关键词，类似链表一样排列着，我们用一个指针指向关键词，访问指针就相当于访问关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        // 4. 调用TokenStream对象的rest方法，用处是将指针指向第一个数据（如果不调用会抛异常）
        tokenStream.reset();

        // 5. 使用while循环遍历TokenStream对象
        while (tokenStream.incrementToken()) {  //返回true说明有下一个元素
            System.out.println(charTermAttribute.toString());
        }

        // 6. 关闭TokenStream
        tokenStream.close();
    }

    @Test
    public void createIndexWithIK() throws Exception {
        //  1. 创建一个Director对象，指定索引库保存的位置。
        Directory directory = FSDirectory.open(new File("D:\\temp\\index").toPath());

        IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
        //  2. 基于Directory对象创建一个IndexWriter对象（用来写索引）
        IndexWriter indexWriter = new IndexWriter(directory, config);

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

}
