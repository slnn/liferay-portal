/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.cache.ehcache;

import static com.liferay.portal.cache.ehcache.SearchablePortalCache.FIELD_UID;

import com.liferay.portal.search.lucene.IndexSearcherManager;
import com.liferay.portal.search.lucene.LuceneHelperUtil;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.RAMDirectory;

/**
 * @author Tina Tian
 */
public class CacheIndexAccessor {

	public CacheIndexAccessor() throws IOException {
		Analyzer analyzer = new LimitTokenCountAnalyzer(
			LuceneHelperUtil.getAnalyzer(),
			PropsValues.LUCENE_ANALYZER_MAX_TOKENS);

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
			LuceneHelperUtil.getVersion(), analyzer);

		_indexWriter = new IndexWriter(new RAMDirectory(), indexWriterConfig);

		_indexSearcherManager = new IndexSearcherManager(_indexWriter);
	}

	public void clear() throws IOException {
		_indexWriter.deleteAll();

		_doCommit();
	}

	public void close() throws IOException {
		_doCommit();

		_indexSearcherManager.close();

		_indexWriter.close();
	}

	public IndexSearcher getIndexSearcher() throws IOException {
		return _indexSearcherManager.acquire();
	}

	public void releaseIndexSearcher(IndexSearcher indexSearcher)
		throws IOException {

		_indexSearcherManager.release(indexSearcher);
	}

	public void removeDocument(Object key) throws IOException {
		Term term = new Term(FIELD_UID, key.toString());

		_indexWriter.deleteDocuments(term);

		_doCommit();
	}

	public void updateDocument(Term term, Document document)
		throws IOException {

		_indexWriter.updateDocument(term, document);

		_doCommit();
	}

	private void _doCommit() throws IOException {
		_commitLock.lock();

		try {
			_indexWriter.commit();
		}
		finally {
			_indexSearcherManager.invalidate();

			_commitLock.unlock();
		}
	}

	private Lock _commitLock = new ReentrantLock();
	private IndexSearcherManager _indexSearcherManager;
	private IndexWriter _indexWriter;

}