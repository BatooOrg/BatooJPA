/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.common.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 *
 * @author hceylan
 * @since 2.0.1
 */
public class BatooUtils {

    private static final UUID uuid = UUID.randomUUID();

    /**
	 * Returns the acronym of the name
	 *
	 * @param name
	 *            the name
	 * @return the acronym
	 *
	 * @since 2.0.1
	 */
	public static String acronym(String name) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			if (Character.isUpperCase(name.charAt(i))) {
				builder.append(name.charAt(i));
			}
		}

		if (builder.length() == 0) {
			builder.append(name.charAt(0));
		}

		return builder.toString();
	}

	/**
	 * Adds all the elements in the source to target.
	 *
	 * @param source
	 *            the source collection
	 * @param target
	 *            the destination collection
	 * @param <E>
	 *            the type of the collections
	 *
	 * @since 2.0.1
	 */
	public static <E> void addAll(Collection<? extends E> source, Collection<E> target) {
		if (source instanceof List) {
			final List<? extends E> list = (List<? extends E>) source;
			for (int i = 0; i < list.size(); i++) {
				target.add(list.get(i));
			}
		}
		else {
			target.addAll(source);
		}
	}

	/**
	 * Indents the <code>string</code> by one <code>tab</code>.
	 *
	 * @param str
	 *            string to indent
	 * @return the indented string
	 *
	 * @since 2.0.1
	 */
	public static String indent(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}

		return "\t" + str.replaceAll("\n", "\n\t");
	}

    /**
     * Load Batoo runtime properties
     *
     * @return runtime properties
     */
    public static Map<String, Object> loadRuntimeProperties(){
        try {
            final InputStream inRuntimeProperties = BatooUtils.class.getClassLoader().getResourceAsStream("batoo-jpa-runtime.properties");
            if (inRuntimeProperties != null) {
                Properties runtimeProperties = new Properties();
                runtimeProperties.load(inRuntimeProperties);
                inRuntimeProperties.close();

                if (runtimeProperties != null) {
                    final Map<String, Object> propertiesMap = Maps.newHashMap();
                    for (final Map.Entry<Object, Object> entry : runtimeProperties.entrySet()) {
                        propertiesMap.put((String) entry.getKey(), entry.getValue());
                    }
                    return propertiesMap;
                }
            }
        } catch (Exception ignored) {
        }
        return Maps.newHashMap();
    }

	/**
	 * Converts the string to lower case.
	 *
	 * @param string
	 *            the string to convert
	 * @return the converted string
	 *
	 * @since 2.0.1
	 */
	public static String lower(String string) {
		return string != null ? string.toLowerCase() : null;
	}

	/**
	 * @param a
	 *            the collection a
	 * @param b
	 *            the collection b
	 * @return the subtracted collection
	 * @param <X>
	 *            the type of the collecions
	 *
	 * @since 2.0.1
	 */
	public static <X> List<X> subtract(final Collection<X> a, final Collection<X> b) {
		final List<X> list = Lists.newArrayList(a);

		for (final Object element : b) {
			list.remove(element);
		}

		return list;
	}

	/**
	 *
	 * @param <X>
	 *            type of key
	 * @param <Y>
	 *            type of value
	 * @param a
	 *            first map
	 * @param b
	 *            second map
	 * @return the subtracted map
	 * @since 2.0.1
	 */
	public static <X, Y> Map<X, Y> subtract(final Map<X, Y> a, final Map<X, Y> b) {
		final Map<X, Y> map = Maps.newHashMap();
		for(Map.Entry<X, Y> entryA : a.entrySet()) {
			final X key = entryA.getKey();
			final Y valueA = entryA.getValue();
			final Y valueB = b.get(key);
			if (!(b.containsKey(key) && valueB.equals(valueA))) {
				map.put(key, valueB);
			}
		}
		return map;
	}

	/**
	 * Indents the <code>string</code> by one <code>tab</code>.
	 *
	 * @param str
	 *            string to indent
	 * @return the indented string
	 *
	 * @since 2.0.1
	 */
	public static String tree(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}

		return "|-->" + str.replaceAll("\n", "\n|   ");
	}

	/**
	 * Converts the string to upper case.
	 *
	 * @param string
	 *            the string to convert
	 * @return the converted string
	 *
	 * @since 2.0.1
	 */
	public static String upper(String string) {
		return string != null ? string.toUpperCase() : null;
	}

    public static void gaBoot(final Map<String, Object> prop){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ga(prop);
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private static int ga(final Map<String, Object> prop) throws Exception {
        final String state = prop.get("batoojpa.state") != null ? ""+prop.get("batoojpa.state") : "boot";
        final String version = "&av="+prop.get("batoojpa.version");
        final String lang = "&ul="+Locale.getDefault().getLanguage();
        final String z = "&z="+ new Random().nextInt();
        final String ua="User-Agent: Java/"+prop.get("java.version")
                + " (" +prop.get("os.name")
                + " )" + "batoo/"+prop.get("batoojpa.build");
        String payload="v=1&tid=UA-41772675-2&cid="+ uuid +"&t=appview&cd="+state+"&an=batoo-jpa"+version+lang+z;
        String url="http://www.google-analytics.com/collect?"+payload;
        ////////
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", ua);
        return con.getResponseCode();
    }
}
