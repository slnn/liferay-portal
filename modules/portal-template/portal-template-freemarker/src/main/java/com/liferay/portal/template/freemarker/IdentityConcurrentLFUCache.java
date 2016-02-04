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

package com.liferay.portal.template.freemarker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Shuyang Zhou
 * @author Dante Wang
 */
public class IdentityConcurrentLFUCache<K, V> {

	public IdentityConcurrentLFUCache(int maxSize) {
		this(maxSize, 0.75F);
	}

	public IdentityConcurrentLFUCache(int maxSize, float loadFactor) {
		this(
			maxSize, loadFactor,
			Runtime.getRuntime().availableProcessors() * 2);
	}

	public IdentityConcurrentLFUCache(
		int maxSize, float loadFactor, int concurrencyLevel) {

		_caches = new LFUCache[concurrencyLevel];

		int maxCacheSize = (int)Math.ceil(
			((double)maxSize) / ((double)concurrencyLevel));

		for (int i = 0; i < concurrencyLevel; i++) {
			_caches[i] = new LFUCache<>(maxCacheSize, loadFactor);
		}
	}

	public void clear() {
		synchronized (this) {
			for (int i = 0; i < _caches.length; i++) {
				_caches[i].clear();
			}
		}
	}

	public V get(K key) {
		return _caches[_hash(key)].get(key);
	}

	public void put(K key, V value) {
		_caches[_hash(key)].put(key, value);
	}

	private int _hash(K object) {
		int hash = System.identityHashCode(object) * 31;

		hash = Math.abs(hash % _caches.length);

		return hash;
	}

	private final LFUCache<K, V>[] _caches;

	private class LFUCache<K, V> {

		public void clear() {
			_writeLock.lock();

			try {
				_cache.clear();
			}
			finally {
				_writeLock.unlock();
			}
		}

		public V get(K key) {
			_readLock.lock();

			try {
				ValueWrapper valueWrapper = _cache.get(key);

				if (valueWrapper != null) {
					valueWrapper._hitCount.getAndIncrement();

					_hitCount.getAndIncrement();

					return valueWrapper._value;
				}
			}
			finally {
				_readLock.unlock();
			}

			_missCount.getAndIncrement();

			return null;
		}

		public void put(K key, V value) {
			if (key == null) {
				throw new NullPointerException("Key is null");
			}

			ValueWrapper valueWrapper = new ValueWrapper(value);

			_writeLock.lock();

			try {
				if (!_cache.containsKey(key) && (_cache.size() >= _maxSize)) {
					_cleanUp();
				}

				_cache.put(key, valueWrapper);
			}
			finally {
				_writeLock.unlock();
			}

			_putCount.getAndIncrement();
		}

		private LFUCache(int maxSize, float loadFactor) {
			if ((maxSize <= 0) || (loadFactor <= 0) || (loadFactor >= 1)) {
				throw new IllegalArgumentException();
			}

			_maxSize = maxSize;
			_expectedSize = (int)(maxSize * loadFactor);

			if (_expectedSize == 0) {
				throw new IllegalArgumentException(
					"maxSize and loadFactor are too small");
			}

			_readLock = _readWriteLock.readLock();
			_writeLock = _readWriteLock.writeLock();
		}

		private void _cleanUp() {
			List<Entry<K, ValueWrapper>> valueWrappers = new ArrayList<>(
				_cache.entrySet());

			Collections.sort(valueWrappers, _entryComparator);

			int cleanUpSize = _cache.size() - _expectedSize;

			_evictCount.getAndAdd(cleanUpSize);

			Iterator<Entry<K, ValueWrapper>> itr = valueWrappers.iterator();

			while ((cleanUpSize-- > 0) && itr.hasNext()) {
				Entry<K, ValueWrapper> entry = itr.next();

				K key = entry.getKey();

				_cache.remove(key);

				itr.remove();
			}
		}

		private final Map<K, ValueWrapper> _cache = new IdentityHashMap<>();
		private final EntryComparator _entryComparator = new EntryComparator();
		private final AtomicLong _evictCount = new AtomicLong();
		private final int _expectedSize;
		private final AtomicLong _hitCount = new AtomicLong();
		private final int _maxSize;
		private final AtomicLong _missCount = new AtomicLong();
		private final AtomicLong _putCount = new AtomicLong();
		private final Lock _readLock;
		private final ReentrantReadWriteLock _readWriteLock =
			new ReentrantReadWriteLock();
		private final Lock _writeLock;

		private class EntryComparator
			implements Comparator<Entry<K, ValueWrapper>> {

			@Override
			public int compare(
				Entry<K, ValueWrapper> entry1, Entry<K, ValueWrapper> entry2) {

				ValueWrapper valueWrapper1 = entry1.getValue();
				ValueWrapper valueWrapper2 = entry2.getValue();

				long hitCount1 = valueWrapper1._hitCount.get();
				long hitCount2 = valueWrapper2._hitCount.get();

				return (int)(hitCount1 - hitCount2);
			}

		}

		private class ValueWrapper {

			public ValueWrapper(V v) {
				_value = v;
			}

			private final AtomicLong _hitCount = new AtomicLong();
			private final V _value;

		}

	}

}