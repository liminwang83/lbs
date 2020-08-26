package com.lbs.index;

import java.io.File;
import java.io.IOException;

import jeasy.analysis.MMAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import com.lbs.util.ConfigUtil;
import com.lbs.util.FileUtil;

/**
 * @author Administrator
 */
public class IndexerImpl implements Indexer
{

    public static void main( final String[] args ) throws Exception
    {
        /*
        File indexDir = new File(ConfigUtil.getProperty("index.dir")); 
        File dataDir = new File(ConfigUtil.getProperty("training.docs.dir")); 
        
        long start = new Date().getTime(); 
        int numIndexed = index(indexDir, dataDir); 
        long end = new Date().getTime(); 
        
        System.out.println("" + numIndexed + ""
            + (end - start) + "milliseconds");*/

        Indexer indexer = new IndexerImpl();
        indexer.buidIndex();
    }

    public void buidIndex() throws Exception
    {

        File dataDir = new File( ConfigUtil.getProperty( "training.docs.dir" ) );
        if( !dataDir.exists() || !dataDir.isDirectory() )
        {
            throw new IOException( dataDir + "does not exist or is not a directory!" );
        }

        File[] categories = dataDir.listFiles();
        for( File indexDir : categories )
        {
            System.out.println( "Building index for " + indexDir.getCanonicalPath() );
            index( indexDir, indexDir );
        }
    }

    // open an index and start file directory traversal
    public static int index( final File indexDir, final File dataDir ) throws IOException
    {
        if( !dataDir.exists() || !dataDir.isDirectory() )
        {
            throw new IOException( dataDir + "does not exist or is not a directory!" );
        }

        IndexWriter writer = new IndexWriter( indexDir, //Lucene 
            new MMAnalyzer(), true ); //using MMAnalyzer instead of StandardAnalyzer
        writer.setUseCompoundFile( true );

        indexDirectory( writer, dataDir );

        int numIndexed = writer.docCount();
        writer.optimize();
        writer.close();
        return numIndexed;
    }

    // recursive method that calls itself when it finds a directory 
    private static void indexDirectory( final IndexWriter writer, final File dir ) throws IOException
    {

        File[] files = dir.listFiles();
        for( int i = 0; i < files.length; i++ )
        {
            File f = files[i];
            if( f.isDirectory() )
            {
                indexDirectory( writer, f );
            }
            else if( f.getName().endsWith( ".txt" ) )
            {
                indexFile( writer, f );
            }
        }
    }

    // method to actually index file using Lucene 
    private static void indexFile( final IndexWriter writer, final File f ) throws IOException
    {

        if( f.isHidden() || !f.exists() || !f.canRead() )
        {
            return;
        }
        System.out.println( "Indexing " + f.getCanonicalPath() );

        Document doc = new Document();
        String text = FileUtil.getText( f );
        doc.add( new Field( "contents", text, Field.Store.YES, Field.Index.TOKENIZED ) );
        doc.add( new Field( "filename", f.getCanonicalPath(), Field.Store.YES, Field.Index.TOKENIZED ) );
        //doc.add(Field.Text("contents", new FileReader(f)));                                                  s
        //doc.add(Field.Keyword("filename", f.getCanonicalPath()));  

        writer.addDocument( doc ); //Lucene     df
    }

}
