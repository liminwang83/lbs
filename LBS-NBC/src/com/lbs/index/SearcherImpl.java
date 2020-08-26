package com.lbs.index;

import java.io.File;

import jeasy.analysis.MMAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.lbs.util.ConfigUtil;

/**
 * @author Administrator
 */
public class SearcherImpl implements Searcher
{

    /**
     * @param args some desc
     * @throws Exception some desc also
     */
    public static void main( final String[] args ) throws Exception
    {

        File indexDir = new File( ConfigUtil.getProperty( "index.dir" ) );
        System.out.println( indexDir.getCanonicalPath() );
        String q = "“Ｘ大奖”基金会因举办私人";

        MMAnalyzer analyzer = new MMAnalyzer();
        System.out.println( analyzer.segment( q, " | " ) );

        if( !indexDir.exists() || !indexDir.isDirectory() )
        {
            throw new Exception( indexDir + " does not exist or is not a directory." );
        }

        if( true )
        {
            return;
        }

        Searcher searcher = new SearcherImpl();
        searcher.search( indexDir, q );
    }

    /**
     * this is javadoc comment for it.
     * 
     * @param indexDir djfdl
     * @param q dfd
     * @throws Exception sdlfjds
     * @return int
     */
    public final int search( final File indexDir, final String q ) throws Exception
    {

        Directory fsDir = FSDirectory.getDirectory( indexDir, false );
        IndexSearcher is = new IndexSearcher( fsDir );
        Analyzer analyzer = new MMAnalyzer(); //instead of StandardAnalyzer()

        QueryParser parser = new QueryParser( "contents", analyzer );
        Query query = parser.parse( q ); //检索词 

        //long start = new Date().getTime(); 
        Hits hits = is.search( query );
        //long end = new Date().getTime(); 
        /*
        System.out.println(hits.length());

        for (int i = 0; i < hits.length(); i++) { 
            Document doc = hits.doc(i);          
            System.out.println(doc.get("filename"));
        } */

        return hits.length();
    }
}
