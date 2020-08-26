package com.lbs.index;

import java.io.File;

public interface Searcher {

	int search(File indexDir, String q) throws Exception;

}
