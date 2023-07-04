// This file is part of RedneckGDX.
// Copyright (C) 2017-2019  Alexander Makarov-[M210] (m210-2007@mail.ru)
//
// RedneckGDX is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// RedneckGDX is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with RedneckGDX.  If not, see <http://www.gnu.org/licenses/>.

package ru.m210projects.Redneck.Types;

import static ru.m210projects.Build.Strhandler.Bstrcmp;
import static ru.m210projects.Build.Strhandler.indexOf;
import static ru.m210projects.Build.Strhandler.toLowerCase;
import static ru.m210projects.Build.OnSceenDisplay.Console.*;
import static ru.m210projects.Redneck.Gamedef.*;
import static ru.m210projects.Redneck.Globals.*;

import java.util.ArrayList;
import java.util.List;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.DirectoryEntry;
import ru.m210projects.Build.FileHandle.FileEntry;
import ru.m210projects.Build.FileHandle.FileUtils;
import ru.m210projects.Build.FileHandle.Group;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.OnSceenDisplay.Console;

public class GameInfo {

	public DirectoryEntry resDir; 
	public String Title;
	
	public EpisodeInfo[] episodes;
	public String[] skillnames;
	public int nEpisodes;
	public String ConName;
	private Script ConScr;
	public boolean isInited = false;
	private int nMaps;
	private FileEntry file;
	private boolean pack;
	
	public GameInfo(FileEntry file, String mainCon)
	{
		this.ConName = mainCon;
		this.Title = mainCon;
		this.resDir = file.getParent();
		this.file = file;
		skillnames = new String[nMaxSkills];
		episodes = new EpisodeInfo[nMaxEpisodes];
		isInited = false;
	}
	
	public GameInfo(Group res, FileEntry file, String mainCon)
	{
		this.ConName = mainCon;
		this.Title = file.getName() + ":" + mainCon;
		skillnames = new String[nMaxSkills];
		episodes = new EpisodeInfo[nMaxEpisodes];

		try {
			List<String> list = new ArrayList<String>();
			list.add(ConName);
			for(int i = 0; i < list.size(); i++) 
				InitTree(list, res, list.get(i));
			
			nMaps = 0;
			nEpisodes = 0;
			for(int i = 0; i < list.size(); i++) {
				Resource fil = res.open(list.get(i));
				if(fil == null) continue;
				
				byte[] data = preparescript(fil.getBytes());
				findSkillNames(data);
				findVolumes(data);
				findMaps(data, res);
				fil.close();
			}

			if(nEpisodes != 0 && nMaps != 0) 
				isInited = true;
			checkEpisodes();
			this.pack = true;
			this.file = file;
		} catch(Exception e) { 
			e.printStackTrace(); 
			Console.Println("Build addon: " + this.Title + " failed!", OSDTEXT_RED);
			isInited = false; 
		}
	}
	
	public FileEntry getFile()
	{
		return file;
	}
	
	public boolean isPackage()
	{
		return pack;
	}
	
	public void setPackage(boolean pack)
	{
		this.pack = pack;
	}

	public void setDirectory(DirectoryEntry resDir)
	{
		this.resDir = resDir;
	}
	
	public DirectoryEntry getDirectory()
	{
		return resDir;
	}
	
	public Script getCON()
	{
		return ConScr;
	}
	
	public void setCON(Script con)
	{
		this.ConScr = con;
	}
	
	public void init()
	{
		try {
			List<FileEntry> list = new ArrayList<FileEntry>();
			list.add(resDir.checkFile(ConName));
			for(int i = 0; i < list.size(); i++)
				InitTree(list, list.get(i));
			
			nMaps = 0;
			nEpisodes = 0;
			for(int i = 0; i < list.size(); i++) {
				FileEntry scriptfile = list.get(i);
				if(scriptfile == null) continue;
				byte[] data = preparescript(BuildGdx.compat.getBytes(scriptfile));
				findSkillNames(data);
				findVolumes(data);
				findMaps(data, null);
			}

			if(nEpisodes != 0 && nMaps != 0) 
				isInited = true;
			checkEpisodes();
			
		} catch(Exception e) { e.printStackTrace(); isInited = false; }
	}
	
	private void checkEpisodes()
	{
		int sum = 0;
		for(int e = 0; e < nEpisodes; e++)
		{
			if(episodes[e] == null)
				continue; //The episode isn't found
			if(episodes[e].gMapInfo[0] == null)
				episodes[e].nMaps = 0;
			sum += episodes[e].nMaps;
		}
		
		if(sum == 0) {
			isInited = false;
		} 
//		else { //sort episodes
//			int e = 0, ep = nEpisodes;
//			while(e != ep)
//			{
//				if(episodes[e] != null && episodes[e].nMaps == 0) {
//					System.arraycopy(episodes, e+1, episodes, e, ep-(e+1));
//					nEpisodes--;
//					episodes[nEpisodes] = null;
//					continue;
//				}
//				e++;
//			}
//		}
	}
	
	private void InitTree(List<FileEntry> list, FileEntry confile)
	{
		if(confile == null) return;
		byte[] buf = preparescript(BuildGdx.compat.getBytes(confile));
		int index = -1;
        while( (index = indexOf("include ", buf, index+1)) != -1)
        {
        	int textptr = index + 7;
        	while( !isaltok(buf[textptr]) )
            {
                textptr++;
                if( buf[textptr] == 0 ) break;
            }

            int i = 0;
            while( textptr+i < buf.length && isaltok(buf[textptr+i]) ) i++;
            String name = new String(buf, textptr, i);
            list.add(resDir.checkFile(name));
        }
	}
	
	private void InitTree(List<String> list, Group res, String filename)
	{
		Resource fil = res.open(filename);
		if(fil != null)
		{
			byte[] buf = preparescript(fil.getBytes());
			int index = -1;
	        while( (index = indexOf("include ", buf, index+1)) != -1)
	        {
	        	int textptr = index + 7;
	        	while( !isaltok(buf[textptr]) )
	            {
	                textptr++;
	                if( buf[textptr] == 0 ) break;
	            }

	            int i = 0;
	            while( textptr+i < buf.length && isaltok(buf[textptr+i]) ) i++;
	            String name = new String(buf, textptr, i);
	            list.add(name);
	        }
	        fil.close();
		}
	}
	
	private boolean findVolumes(byte[] buf)
	{
        int index = -1;

        while( (index = indexOf("definevolumename ", buf, index+1)) != -1)
        {
        	textptr = index + 16;
            Integer j = transnum(buf);
            if(j == null) continue;
            while( buf[textptr] == ' ' ) textptr++;

            int i = 0;
            int startptr = textptr;
            while( buf[textptr+i] != 0x0a ) i++;
            
            episodes[j] = new EpisodeInfo(new String(buf, startptr, i-1).toUpperCase());
            nEpisodes = Math.max(nEpisodes, j + 1);
        }
        
        if(nEpisodes != 0) 
	        return true;
        
        return false;
	}
	
	private void findMaps(byte[] buf, Group res)
	{
		int index = -1;
		
        while( (index = indexOf("definelevelname ", buf, index+1)) != -1)
        {
        	textptr = index + 15;
            Integer epnum = transnum(buf);
            if(epnum == null) continue;
            Integer mapnum = transnum(buf);
            if(mapnum == null) continue;
            
            while( buf[textptr] == ' ' ) textptr++;

            int i = 0;
            int ptr = textptr;
            while( buf[textptr] != ' ' && buf[textptr] != 0x0a ) { textptr++; i++; }
            
            String path = FileUtils.getCorrectPath(toLowerCase(new String(buf, ptr, i)));
            boolean mapFound = false;
            String mapPath = path;
            if(res == null)
            {
            	FileEntry mapFile = resDir.checkFile(path);
            	mapFound = mapFile != null;
            	if(mapFound)
            		mapPath = mapFile.getPath();
            } else {
            	mapFound = res.contains(path);
            	mapPath = path;
            }
            
			if(mapFound && episodes[epnum] != null) {
	            while( buf[textptr] == ' ' ) textptr++;
	
	            int partime = (((buf[textptr+0]-'0')*10+(buf[textptr+1]-'0'))*26*60)+
	                (((buf[textptr+3]-'0')*10+(buf[textptr+4]-'0'))*26);
	
	            textptr += 5;
	            while( buf[textptr] == ' ' ) textptr++;
	
	            int designertime =
	            	(((buf[textptr+0]-'0')*10+(buf[textptr+1]-'0'))*26*60)+
	            	(((buf[textptr+3]-'0')*10+(buf[textptr+4]-'0'))*26);
	
	            textptr += 5;
	            while( buf[textptr] == ' ' ) textptr++;
	
	            i = 0;
	            while( buf[textptr+i] != 0x0a ) i++;
	            String title = new String(buf, textptr, i-1);
	            episodes[epnum].gMapInfo[mapnum] = new MapInfo(mapPath, title, partime, designertime);
	            episodes[epnum].nMaps = Math.max(episodes[epnum].nMaps, mapnum + 1);
	            nMaps++;
			}
        }
	}
	
	private boolean findSkillNames(byte[] buf)
	{
		int index = -1;
		int size = 0;
        while( (index = indexOf("defineskillname ", buf, index+1)) != -1)
        {
        	textptr = index + 15;
        	Integer j = transnum(buf);
            if(j == null) continue;
            while( buf[textptr] == ' ' ) textptr++;

            int i = 0;
            while( buf[textptr+i] != 0x0a ) i++;
            
            skillnames[j] = new String(buf, textptr, i-1).toUpperCase();
            size = Math.max(size, j+1);  
        }
        
        if(size != 0)
        	return true;

        return false;
	}
	private int textptr;
	private char[] tempbuf = new char[2048];
	private Integer transnum(byte[] text)
	{
		while( !isaltok(text[textptr]) )
	    {
	    	textptr++;
	        if( text[textptr] == 0 )
	            return null;
	    }

	    int l = 0;
	    while( isaltok(text[textptr + l]) )
	    {
	        tempbuf[l] = (char) text[textptr + l];
	        l++;
	    }
	    
	    tempbuf[l] = 0;
	    for(int i=0;i<NUMKEYWORDS;i++)
	        if( Bstrcmp( label, (labelcnt<<6), keyw[i], 0) == 0 )
	    {
	        error++;
	        Console.Println("  * ERROR! Symbol '" + label[(labelcnt<<6)] + "' is a key word.");
	        textptr+=l;
	    }

	    for(int i=0;i<labelcnt;i++)
	    {
	        if( Bstrcmp(tempbuf, 0, label, i<<6) == 0 )
	        {
	        	textptr += l;
	            return labelcode.get(i);
	        }
	    }

	    if( !Character.isDigit(text[textptr]) && text[textptr] != '-')
	    {
	    	Console.Println("  * ERROR! Parameter '" + new String(tempbuf, 0, l) + "' is undefined.");
	        error++;
	        textptr+=l;
	        return null;
	    }
	    
	    String number = new String(text, textptr, l);
	    textptr += l;
	    return Integer.parseInt(number);
	}
	
}
