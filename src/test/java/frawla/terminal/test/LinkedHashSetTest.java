package frawla.terminal.test;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashSet;

import org.junit.Test;

import frawla.terminal.core.RecentFile;

public class LinkedHashSetTest
{
	class Person
	{
		String name = "";
		int age  =0;
		Person(String n, int a){
			name = n;
			age = a;
		}
		@Override
		public String toString()
		{
			return name;
		}

		@Override
		public int hashCode() 
		{
			final int prime  = 31;
			int result = 1;

			result = prime * result + 
					((name == null) ? 0 : name.hashCode());

			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;

			if (obj == null)
				return false;

			if (getClass() != obj.getClass()) {
				return false;
			}

			Person other = (Person) obj;
			if (!other.name.equals(name))
				return false;

			return true;		
		}
	}

	@Test
	public void HashSetOrderTest() 
	{
		LinkedHashSet<String> hs = new LinkedHashSet<>();
		// add elements to the hash set
		hs.add("B");
		hs.add("A");
		hs.add("D");
		hs.add("E");
		hs.add("C");
		hs.add("A");
		hs.add("A");
		hs.add("B");
		hs.add("F");

		assertEquals("[B, A, D, E, C, F]", hs.toString() );

		StringBuilder sb = new StringBuilder();
		hs.stream()
		.sorted()
		.forEach(e -> sb.append(e + ", "));

		assertEquals("A, B, C, D, E, F, ", sb.toString());
	}

	@Test
	public void HashSetRecentAddedTest() 
	{
		LinkedHashSet<Person> hs = new LinkedHashSet<>();

		// add elements to the hash set
		hs.add(new Person("sami", 10));
		hs.add(new Person("Ali", 12));
		hs.add(new Person("Ali", 12));
		hs.add(new Person("Kareem", 20));
		hs.add(new Person("Shamel", 14));

		assertEquals("[sami, Ali, Kareem, Shamel]", hs.toString());

		StringBuilder sb = new StringBuilder();
		hs	.stream()
		.sorted((p1, p2) -> p2.age - p1.age)
		.forEach(p -> sb.append(p + ", "));

		assertEquals("Kareem, Shamel, Ali, sami, ", sb.toString());
	}


	@Test
	public void HashSetRecentAddedFilesTest() 
	{
		LinkedHashSet<RecentFile> hs = new LinkedHashSet<>();

		// add elements to the hash set
		hs.add(new RecentFile("hi.cat", 1));
		hs.add(new RecentFile("bebo", 2));
		hs.add(new RecentFile("memo", 3));

		assertEquals("[hi.cat, bebo, memo]", hs.toString());

		StringBuilder sb = new StringBuilder();
		hs	.stream()
			.sorted((f1, f2) -> f2.id - f1.id)
			.forEach(f -> sb.append(f + ", "));

		assertEquals("memo, bebo, hi.cat, ", sb.toString());
	}

}
