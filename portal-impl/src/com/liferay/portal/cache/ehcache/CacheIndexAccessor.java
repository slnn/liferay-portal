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

import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.lucene.FieldWeightSimilarity;
import com.liferay.portal.search.lucene.LuceneHelperUtil;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.SetBasedFieldSelector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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
	}

	public void clear() throws IOException {
		_indexWriter.deleteAll();

		_doCommit();
	}

	public void close() throws IOException {
		_doCommit();

		_indexWriter.close();
	}

	public void removeDocument(Object key) throws IOException {
		Term term = new Term(FIELD_UID, key.toString());

		_indexWriter.deleteDocuments(term);

		_doCommit();
	}

	public Set<String> search(Query query) throws IOException {
		IndexSearcher indexSearcher = new IndexSearcher(
			IndexReader.open(_indexWriter.getDirectory(), true));

		indexSearcher.setDefaultFieldSortScoring(true, false);
		indexSearcher.setSimilarity(new FieldWeightSimilarity());

		TopDocs topdocs = indexSearcher.search(
			query, null, PropsValues.INDEX_SEARCH_LIMIT);
		ScoreDoc[] scoreDocs = topdocs.scoreDocs;

		int totalHits = topdocs.totalHits;

		if (totalHits <= 0) {
			return Collections.emptySet();
		}

		FieldSelector fieldSelector = new SetBasedFieldSelector(
			SetUtil.fromArray(new String[]{SearchablePortalCache.FIELD_UID}),
			Collections.<String>emptySet());

		Set<String> results = new HashSet<String>();

		for (int i = 0; i < totalHits; i++) {
			int docId = scoreDocs[i].doc;

			Document document = indexSearcher.doc(docId, fieldSelector);

			results.add(document.get(SearchablePortalCache.FIELD_UID));
		}

		return results;
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
			_commitLock.unlock();
		}
	}

	private Lock _commitLock = new ReentrantLock();
	private IndexWriter _indexWriter;

}