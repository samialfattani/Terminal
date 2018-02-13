package frawla.terminal.core;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

public class RecentFile extends File implements Serializable
{
	private static final long serialVersionUID = -839532934342931010L;
	public int id  = 0;
	public RecentFile(String path, int a){
		super(path);
		id = a;
	}
	public RecentFile(URI uri, int a){
		super(uri);
		id = a;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public int hashCode() 
	{
		final int prime  = 31;
		int result = 1;

		result = prime * result + 
				((getAbsolutePath() == null) ? 0 : getAbsolutePath().hashCode());

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

		RecentFile other = (RecentFile) obj;
		if (!other.getAbsolutePath().equals(getAbsolutePath()))
			return false;

		return true;		
	}
}
