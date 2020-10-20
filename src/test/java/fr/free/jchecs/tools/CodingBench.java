/*
 jChecs: a simple Java chess game sample

 Copyright (C) 2006-2017 by David Cotton

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package fr.free.jchecs.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Classe utilitaire permettant de tester des optimisations de code.
 * 
 * @author David Cotton
 */
@SuppressWarnings({"ForLoopReplaceableByForEach", "StringBufferMayBeStringBuilder", "UnnecessaryBoxing", "UnnecessaryUnboxing", "WhileLoopReplaceableByForEach", "ManualArrayCopy", "MismatchedReadAndWriteOfArray", "UnusedAssignment"})
final class CodingBench {
	/** Pour tester la vitesse de traitement des variables statiques. */
	private static int S_var;

	/** Pour tester la vitesse de traitement des variables d'instance. */
	private int _var;

	/**
	 * Classe utilitaire : ne pas instancier.
	 */
	private CodingBench() {
		// Rien de spécifique...
	}

	/**
	 * Mesure des performances de l'allocation des des tableaux.
	 */
	private static void benchArrayAllocations() {
		final int nbBoucles = 200000;

		System.out.println("Array allocations benchmark :");
		System.out.print(" - Integer[64][64]  : ");
		long debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final Integer[][] tab = new Integer[64][64];
		}
		long fin = System.currentTimeMillis();
		final long ref = fin - debut;
		System.out.println("100% (" + ref + "ms)");

		System.out.print(" - Integer[64 * 64] : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final Integer[] tab = new Integer[64 * 64];
		}
		fin = System.currentTimeMillis();
		long duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Byte[64 * 64] : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final Byte[] tab = new Byte[64 * 64];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Long[64 * 64] : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final Long[] tab = new Long[64 * 64];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Integer[32 * 64] : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final Integer[] tab = new Integer[32 * 64];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Integer[0]       : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final Integer[] tab = new Integer[0];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - int[64][64]      : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final int[][] tab = new int[64][64];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - int[64 * 64]     : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final int[] tab = new int[64 * 64];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - byte[64 * 64]     : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final byte[] tab = new byte[64 * 64];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - long[64 * 64]     : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final long[] tab = new long[64 * 64];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - int[32 * 64]     : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final int[] tab = new int[32 * 64];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - int[0]           : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final int[] tab = new int[0];
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

	}

	/**
	 * Mesure des performances de la copie de tableaux.
	 */
	private static void benchArrayCopies() {
		final int nbItems = 150000;
		final int nbBoucles = 2000;

		final Integer[] srcObj = new Integer[nbItems];
		final int[] srcInt = new int[nbItems];
		for (int i = nbItems; --i >= 0; /* Pré-décrémenté */) {
			srcObj[i] = Integer.valueOf(i);
			srcInt[i] = i;
		}

		System.out.println("Array copies benchmark :");
		System.out.print(" - System.arraycopy(Integer) : ");
		long debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			final int l = srcObj.length;
			final Integer[] dst = new Integer[l];
			System.arraycopy(srcObj, 0, dst, 0, l);
		}
		long fin = System.currentTimeMillis();
		final long ref = fin - debut;
		System.out.println("100% (" + ref + "ms)");

		System.out.print(" - Arrays.copyOf(Integer)    : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final Integer[] dst = Arrays.copyOf(srcObj, srcObj.length);
		}
		fin = System.currentTimeMillis();
		long duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - for (Integer, forward)    : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			final int l = srcObj.length;
			final Integer[] dst = new Integer[l];
			for (int idx = 0; idx < l; idx++) {
				dst[idx] = srcObj[idx];
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - for (Integer, backward)   : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			final int l = srcObj.length;
			final Integer[] dst = new Integer[l];
			for (int idx = l; --idx >= 0; /* Pré-décrémenté */) {
				dst[idx] = srcObj[idx];
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - System.arraycopy(int)     : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			final int l = srcInt.length;
			final int[] dst = new int[l];
			System.arraycopy(srcInt, 0, dst, 0, l);
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Arrays.copyOf(int)        : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			@SuppressWarnings("unused")
			final int[] dst = Arrays.copyOf(srcInt, srcInt.length);
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - for (int, forward)        : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			final int l = srcInt.length;
			final int[] dst = new int[l];
			for (int idx = 0; idx < l; idx++) {
				dst[idx] = srcInt[idx];
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - for (int, backward)       : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			final int l = srcInt.length;
			final int[] dst = new int[l];
			for (int idx = l; --idx >= 0; /* Pré-décrémenté */) {
				dst[idx] = srcInt[idx];
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");
	}

	/**
	 * Mesure de la vitesse de l'autoboxing.
	 */
	@SuppressWarnings("boxing")
	private static void benchAutoboxing() {
		final int nbBoucles = 50000000;

		System.out.println("Autoboxing :");

		System.out.print(" - Integer = new Integer(int)     : ");
		long debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			Integer o = new Integer(nb);
			int i = o.intValue() + 1;
			o = new Integer(i - 1);
			i = o.intValue() - 1;
			o = new Integer(i + 1);
			i = o.intValue() + 1;
			o = new Integer(i - 1);
			i = o.intValue() - 1;
			o = new Integer(i + 1);
			i = o.intValue() + 1;
			o = new Integer(i - 1);
			i = o.intValue() - 1;
			o = new Integer(i + 1);
			i = o.intValue() + 1;
			o = new Integer(i - 1);
			i = o.intValue() - 1;
		}
		long fin = System.currentTimeMillis();
		final long ref = fin - debut;
		System.out.println("100% (" + ref + "ms)");

		System.out.print(" - Integer = Integer.valueOf(int) : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			Integer o = Integer.valueOf(nb);
			int i = o.intValue() + 1;
			o = Integer.valueOf(i - 1);
			i = o.intValue() - 1;
			o = Integer.valueOf(i + 1);
			i = o.intValue() + 1;
			o = Integer.valueOf(i - 1);
			i = o.intValue() - 1;
			o = Integer.valueOf(i + 1);
			i = o.intValue() + 1;
			o = Integer.valueOf(i - 1);
			i = o.intValue() - 1;
			o = Integer.valueOf(i + 1);
			i = o.intValue() + 1;
			o = Integer.valueOf(i - 1);
			i = o.intValue() - 1;
		}
		fin = System.currentTimeMillis();
		long duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Integer = int                  : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			Integer o = nb;
			int i = o + 1;
			o = i - 1;
			i = o - 1;
			o = i + 1;
			i = o + 1;
			o = i - 1;
			i = o - 1;
			o = i + 1;
			i = o + 1;
			o = i - 1;
			i = o - 1;
			o = i + 1;
			i = o + 1;
			o = i - 1;
			i = o - 1;
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - int = int                      : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			int o = nb;
			int i = o + 1;
			o = i - 1;
			i = o - 1;
			o = i + 1;
			i = o + 1;
			o = i - 1;
			i = o - 1;
			o = i + 1;
			i = o + 1;
			o = i - 1;
			i = o - 1;
			o = i + 1;
			i = o + 1;
			o = i - 1;
			i = o - 1;
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");
	}

	/**
	 * Mesure de la vitesse de concatenation des chaînes.
	 */
	private static void benchConcatenations() {
		final int nbBoucles = 2000000;

		final String s1 = "Tests number ";
		final String s2 = " of concatenation.";

		System.out.println("Concatenation of variables benchmark :");
		System.out.print(" - String        : ");
		long debut = System.currentTimeMillis();
		for (int i = 0; i < nbBoucles; i++) {
			String res = s1 + Integer.toString(i) + s2;
			res += res;
			res = res + res + res + res;
		}
		long fin = System.currentTimeMillis();
		final long ref = fin - debut;
		System.out.println("100% (" + ref + "ms)");

		System.out.print(" - StringBuffer  : ");
		debut = System.currentTimeMillis();
		for (int i = 0; i < nbBoucles; i++) {
			final StringBuffer sb = new StringBuffer(s1).append(
					Integer.toString(i)).append(s2);
			sb.append(sb);
			sb.append(sb).append(sb);
			@SuppressWarnings("unused")
			final String res = sb.toString();
		}
		fin = System.currentTimeMillis();
		long duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - StringBuilder : ");
		debut = System.currentTimeMillis();
		for (int i = 0; i < nbBoucles; i++) {
			final StringBuilder sb = new StringBuilder(s1).append(
					Integer.toString(i)).append(s2);
			sb.append(sb);
			sb.append(sb).append(sb);
			@SuppressWarnings("unused")
			final String res = sb.toString();
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.println("Concatenation of constants benchmark :");
		System.out.print(" - String        : ");
		debut = System.currentTimeMillis();
		for (int i = 0; i < nbBoucles; i++) {
			String res = s1 + s2 + s1 + s2;
			res += res;
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - StringBuffer  : ");
		debut = System.currentTimeMillis();
		for (int i = 0; i < nbBoucles; i++) {
			final StringBuffer sb = new StringBuffer(s1).append(s2);
			sb.append(sb);
			sb.append(sb);
			@SuppressWarnings("unused")
			final String res = sb.toString();
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - StringBuilder : ");
		debut = System.currentTimeMillis();
		for (int i = 0; i < nbBoucles; i++) {
			final StringBuilder sb = new StringBuilder(s1).append(s2);
			sb.append(sb);
			sb.append(sb);
			@SuppressWarnings("unused")
			final String res = sb.toString();
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");
	}

	/**
	 * Mesure des performances des génériques.
	 */
	private static void benchGenerics() {
		final int nbItems = 1000000;
		final int nbBoucles = 300;

		System.out.println("Generics (getting) :");
		final List<Integer> listInt = new ArrayList<>(nbItems);
		final List<Object> listObj = new ArrayList<>(nbItems);
		final List<? super Number> listQst = new ArrayList<>(nbItems);
		for (int i = 0; i < nbItems; i++) {
			final Integer val = Integer.valueOf(i);
			listInt.add(val);
			listObj.add(val);
			listQst.add(val);
		}

		System.out.print(" - Integer = List<Integer>        : ");
		long debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < listInt.size(); idx++) {
				@SuppressWarnings("unused")
				final Integer res = listInt.get(idx);
			}
		}
		long fin = System.currentTimeMillis();
		final long ref = fin - debut;
		System.out.println("100% (" + ref + "ms)");

		System.out.print(" - Integer = List<Object>         : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < listObj.size(); idx++) {
				@SuppressWarnings("unused")
				final Integer res = (Integer) listObj.get(idx);
			}
		}
		fin = System.currentTimeMillis();
		long duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Integer = List<? super Number> : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < listQst.size(); idx++) {
				@SuppressWarnings("unused")
				final Integer res = (Integer) listQst.get(idx);
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Object = List<Integer>         : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < listInt.size(); idx++) {
				@SuppressWarnings("unused")
				final Object res = listInt.get(idx);
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Object = List<Object>          : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < listObj.size(); idx++) {
				@SuppressWarnings("unused")
				final Object res = listObj.get(idx);
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Object = List<? super Number>  : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < listQst.size(); idx++) {
				@SuppressWarnings("unused")
				final Object res = listQst.get(idx);
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.println("Generics (setting) :");
		final Integer zero = Integer.valueOf(0);

		System.out.print(" - List<Integer> = Integer       : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < listInt.size(); idx++) {
				listInt.set(idx, zero);
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - List<Object> = Integer         : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < listObj.size(); idx++) {
				listObj.set(idx, zero);
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - List<? super Number> = Integer : ");
		debut = System.currentTimeMillis();
		for (int nb = nbBoucles; --nb >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < listQst.size(); idx++) {
				listQst.set(idx, zero);
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");
	}

	/**
	 * Mesure des différentes façons de parcours une liste d'éléments.
	 */
	private static void benchIterations() {
		final int nbItems = 1000000;
		final int nbBoucles = 200;

		System.out.println("Iterating over an ArrayList of Integer :");
		final List<Integer> list = new ArrayList<>(nbItems);
		for (int i = 0; i < nbItems; i++) {
			list.add(Integer.valueOf(i));
		}

		System.out.print(" - Java 1.0 Enumeration (for)   : ");
		long debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final Enumeration<Integer> e = Collections.enumeration(list); e
					.hasMoreElements(); /**/) {
				@SuppressWarnings("unused")
				final int res = e.nextElement().intValue() + 1;
			}
		}
		long fin = System.currentTimeMillis();
		final long ref = fin - debut;
		System.out.println("100% (" + ref + "ms)");

		System.out.print(" - Java 1.0 Enumeration (while) : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			final Enumeration<Integer> en = Collections.enumeration(list);
			while (en.hasMoreElements()) {
				@SuppressWarnings("unused")
				final int res = en.nextElement().intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		long duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Java 1.2 Iterator (for)      : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final Integer integer : list) {
				@SuppressWarnings("unused")
				final int res = integer.intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Java 1.2 Iterator (while)    : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			final Iterator<Integer> it = list.iterator();
			while (it.hasNext()) {
				@SuppressWarnings("unused")
				final int res = it.next().intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - J2SE 5.0 Enhanced for Loop   : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final Integer i : list) {
				@SuppressWarnings("unused")
				final int res = i.intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Indexed access (forward)     : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < list.size(); idx++) {
				@SuppressWarnings("unused")
				final int res = list.get(idx).intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Indexed access (backward)    : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (int idx = list.size(); --idx >= 0; /* Pré-décrémenté */) {
				@SuppressWarnings("unused")
				final int res = list.get(idx).intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.println("Iterating over a Vector of Integer :");
		final Vector<Integer> vect = new Vector<>();
		for (int i = 0; i < nbItems; i++) {
			vect.add(Integer.valueOf(i));
		}

		System.out.print(" - Java 1.0 Enumeration (for)   : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final Integer integer : vect) {
				@SuppressWarnings("unused")
				final int res = integer.intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Java 1.0 Enumeration (while) : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			final Enumeration<Integer> en = vect.elements();
			while (en.hasMoreElements()) {
				@SuppressWarnings("unused")
				final int res = en.nextElement().intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Java 1.2 Iterator (for)      : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final Integer integer : vect) {
				@SuppressWarnings("unused")
				final int res = integer.intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Java 1.2 Iterator (while)    : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			final Iterator<Integer> it = vect.iterator();
			while (it.hasNext()) {
				@SuppressWarnings("unused")
				final int res = it.next().intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - J2SE 5.0 Enhanced for Loop   : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final Integer i : vect) {
				@SuppressWarnings("unused")
				final int res = i.intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Indexed access (forward)     : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (int idx = 0; idx < vect.size(); idx++) {
				@SuppressWarnings("unused")
				final int res = vect.get(idx).intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Indexed access (backward)    : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (int idx = vect.size(); --idx >= 0; /* Pré-décrémenté */) {
				@SuppressWarnings("unused")
				final int res = vect.get(idx).intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.println("Iterating over an array of Integer :");
		final Integer[] tabObj = list.toArray(new Integer[list.size()]);

		System.out.print(" - J2SE 5.0 Enhanced for Loop : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final Integer i : tabObj) {
				@SuppressWarnings("unused")
				final int res = i.intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Indexed access (forward)   : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final Integer element : tabObj) {
				@SuppressWarnings("unused")
				final int res = element.intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Indexed access (backward)  : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (int idx = tabObj.length; --idx >= 0; /* Pré-décrémenté */) {
				@SuppressWarnings("unused")
				final int res = tabObj[idx].intValue() + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.println("Iterating over an array of int :");
		final int[] tabInt = new int[nbItems];
		for (int i = 0; i < nbItems; i++) {
			tabInt[i] = i;
		}

		System.out.print(" - J2SE 5.0 Enhanced for Loop : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final int i : tabInt) {
				@SuppressWarnings("unused")
				final int res = i + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Indexed access (forward)   : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (final int element : tabInt) {
				@SuppressWarnings("unused")
				final int res = element + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - Indexed access (backward)  : ");
		debut = System.currentTimeMillis();
		for (int l = nbBoucles; --l >= 0; /* Pré-décrémenté */) {
			for (int idx = tabInt.length; --idx >= 0; /* Pré-décrémenté */) {
				@SuppressWarnings("unused")
				final int res = tabInt[idx] + 1;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");
	}

	/**
	 * Mesure des performances des boucles.
	 */
	private static void benchLoops() {
		final int nbBoucles = 40000000;

		System.out.println("Loops benchmark :");
		System.out.print(" - for (forward)    : ");
		long debut = System.currentTimeMillis();
		for (int i = 0; i < nbBoucles; i++) {
			for (int j = 0; j < nbBoucles; j++) {
				@SuppressWarnings("unused")
				final int a = 0;
			}
		}
		long fin = System.currentTimeMillis();
		final long ref = fin - debut;
		System.out.println("100% (" + ref + "ms)");

		System.out.print(" - for (backward)   : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			for (int j = nbBoucles; --j >= 0; /* Pré-décrémenté */) {
				@SuppressWarnings("unused")
				final int a = 0;
			}
		}
		fin = System.currentTimeMillis();
		long duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - for (backward-2) : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles - 1; i >= 0; i--) {
			for (int j = nbBoucles - 1; j >= 0; j--) {
				@SuppressWarnings("unused")
				final int a = 0;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - while (forward)  : ");
		debut = System.currentTimeMillis();
		int i = 0;
		while (i++ < nbBoucles) {
			int j = 0;
			while (j++ < nbBoucles) {
				@SuppressWarnings("unused")
				final int a = 0;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - while (backward) : ");
		debut = System.currentTimeMillis();
		i = nbBoucles;
		while (--i >= 0) {
			int j = nbBoucles;
			while (--j >= 0) {
				@SuppressWarnings("unused")
				final int a = 0;
			}
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");
	}

	/**
	 * Teste les performances pour l'accès aux variables.
	 */
	private void benchVariables() {
		final int nbBoucles = 400000000;

		System.out.println("Variables benchmark :");
		System.out.print(" - static   : ");
		long debut = System.currentTimeMillis();
		for (int i = 0; i < nbBoucles; i++) {
			S_var++;
			S_var = S_var * 2;
			S_var /= 2;
			S_var = S_var - 1;
		}
		long fin = System.currentTimeMillis();
		final long ref = fin - debut;
		System.out.println("100% (" + ref + "ms)");

		System.out.print(" - instance : ");
		debut = System.currentTimeMillis();
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			_var++;
			_var = _var * 2;
			_var /= 2;
			_var = _var - 1;
		}
		fin = System.currentTimeMillis();
		long duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");

		System.out.print(" - local    : ");
		debut = System.currentTimeMillis();
		int var = 0;
		for (int i = nbBoucles; --i >= 0; /* Pré-décrémenté */) {
			var++;
			var = var * 2;
			var /= 2;
			var = var - 1;
		}
		fin = System.currentTimeMillis();
		duree = Math.max(fin - debut, 1);
		System.out.println((100 * ref / duree) + "% (" + duree + "ms)");
	}

	/**
	 * Lance les différents tests de performance.
	 * 
	 * @param pArgs
	 *            Arguments de la ligne de commande : ignorés, aucun argument
	 *            attendu.
	 */
	public static void main(final String[] pArgs) {
		benchArrayAllocations();
		benchArrayCopies();
		benchAutoboxing();
		benchConcatenations();
		benchGenerics();
		benchIterations();
		benchLoops();
		new CodingBench().benchVariables();
	}
}
