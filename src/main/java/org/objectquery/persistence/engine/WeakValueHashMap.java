package org.objectquery.persistence.engine;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Partial implementation of a weak value hashmap, mostly copied from
 * @{WeakHashMap}
 * 
 * 
 * @author tglman
 *
 */
public class WeakValueHashMap {

	private Entry<Object, Object>[] table;
	private int size;
	private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
	private int threshold;

	@SuppressWarnings("unchecked")
	public WeakValueHashMap() {
		table = new Entry[16];
		threshold = (int) (16 * 0.75);
	}

	private static class Entry<K, V> extends WeakReference<V> {
		private Object key;
		private Entry<K, V> next;
		private int hash;

		public Entry(K k, V v, ReferenceQueue<V> queue, int hash, Entry<K, V> next) {
			super(v, queue);
			this.key = k;
			this.hash = hash;
			this.next = next;
		}

		private Object getValue() {
			return get();
		}

		public Object getKey() {
			return key;
		}
	}

	public Object get(Object key) {
		Object k = key;// maskNull(key);
		int h = hash(k);
		Entry<Object, Object>[] tab = getTable();
		int index = indexFor(h, tab.length);
		Entry<Object, Object> e = tab[index];
		while (e != null) {
			if (e.hash == h && eq(k, e.getKey()))
				return e.getValue();
			e = e.next;
		}
		return null;
	}

	private int indexFor(int h, int length) {
		return h & (length - 1);
	}

	private int hash(Object k) {
		return k.hashCode();
	}

	private Entry<Object, Object>[] getTable() {
		expungeStaleEntries();
		return table;
	}

	public Object put(Object key, Object value) {
		Object k = key; // maskNull(key);
		int h = hash(k);
		Entry<Object, Object>[] tab = getTable();
		int i = indexFor(h, tab.length);
		Entry<Object, Object> prev = tab[i];
		for (Entry<Object, Object> e = tab[i]; e != null; e = e.next) {
			if (h == e.hash && eq(k, e.getKey())) {
				Object oldValue = e.getValue();
				if (value != oldValue) {
					if (e == tab[i])
						tab[i] = new Entry<>(k, value, queue, h, e.next);
					else
						prev.next = new Entry<>(k, value, queue, h, e.next);
				}
				return oldValue;
			}
			prev = e;
		}

		// modCount++;
		Entry<Object, Object> e = tab[i];
		tab[i] = new Entry<>(k, value, queue, h, e);
		if (++size >= threshold)
			resize(tab.length * 2);
		return null;
	}

	public int size() {
		return size;
	}

	private static boolean eq(Object x, Object y) {
		return x == y || x.equals(y);
	}

	void resize(int newCapacity) {
		Entry<Object, Object>[] oldTable = getTable();
		int oldCapacity = oldTable.length;

		Entry<Object, Object>[] newTable = new Entry[newCapacity];
		transfer(oldTable, newTable);
		table = newTable;

		/*
		 * If ignoring null elements and processing ref queue caused massive
		 * shrinkage, then restore old table. This should be rare, but avoids
		 * unbounded expansion of garbage-filled tables.
		 */
		if (size >= threshold / 2) {
			threshold = (int) (newCapacity * 0.75);
		} else {
			expungeStaleEntries();
			transfer(newTable, oldTable);
			table = oldTable;
		}
	}

	/** Transfers all entries from src to dest tables */
	private void transfer(Entry<Object, Object>[] src, Entry<Object, Object>[] dest) {
		for (int j = 0; j < src.length; ++j) {
			Entry<Object, Object> e = src[j];
			src[j] = null;
			while (e != null) {
				Entry<Object, Object> next = e.next;
				Object key = e.getKey();
				if (key == null) {
					e.next = null; // Help GC
					// e.value = null; // "   "
					size--;
				} else {
					int i = indexFor(e.hash, dest.length);
					e.next = dest[i];
					dest[i] = e;
				}
				e = next;
			}
		}
	}

	private void expungeStaleEntries() {
		for (Object x; (x = queue.poll()) != null;) {
			synchronized (queue) {
				@SuppressWarnings("unchecked")
				Entry<Object, Object> e = (Entry<Object, Object>) x;
				int i = indexFor(e.hash, table.length);

				Entry<Object, Object> prev = table[i];
				Entry<Object, Object> p = prev;
				while (p != null) {
					Entry<Object, Object> next = p.next;
					if (p == e) {
						if (prev == e)
							table[i] = next;
						else
							prev.next = next;
						// Must not null out e.next;
						// stale entries may be in use by a HashIterator
						// e.value = null; // Help GC
						size--;
						break;
					}
					prev = p;
					p = next;
				}
			}
		}
	}
}
