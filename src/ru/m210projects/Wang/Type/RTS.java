package ru.m210projects.Wang.Type;

import static ru.m210projects.Wang.Main.*;
import static ru.m210projects.Build.OnSceenDisplay.Console.OSDTEXT_RED;

import ru.m210projects.Build.Architecture.BuildGdx;
import ru.m210projects.Build.FileHandle.Resource;
import ru.m210projects.Build.FileHandle.Resource.Whence;
import ru.m210projects.Build.OnSceenDisplay.Console;
import ru.m210projects.Build.Types.LittleEndian;

public class RTS {

	/*
	============================================================================

	                                                LUMP BASED ROUTINES

	============================================================================
	*/
	
	public static int numlumps;
	public static LumpInfo[] lumpinfo; // location of each lump on disk
	public static byte[][] lumpcache;
	public static boolean RTS_Started = false;
	public static char rtsplaying;

	public static byte[] lumplockbyte = new byte[13];

	/*
	====================
	=
	= RTS_AddFile
	=
	= All files are optional, but at least one file must be found
	= Files with a .rts extension are wadlink files with multiple lumps
	= Other files are single lumps with the base filename for the lump name
	=
	====================
	*/
	
	public static boolean RTS_AddFile(String filename)
	{
		Resource handle = BuildGdx.cache.open(filename, 0);
		if (handle == null) {
			Console.Println("RTS file " + filename + " was not found", OSDTEXT_RED);
			return false;
		}
		
		int startlump = numlumps;

		// WAD file
		Console.Println("    Adding " + filename);
		byte[] data = new byte[16];
		handle.read( data, 0, 12 );
		//WadInfo header = new WadInfo(data);
		
		String identification = new String(data, 0, 4);
		int infonumlumps = LittleEndian.getInt(data, 4);
		int infotableofs = LittleEndian.getInt(data, 8);
		
		if (!identification.equalsIgnoreCase("IWAD")) {
			Console.Println("RTS file " + filename + " doesn't have IWAD id", OSDTEXT_RED);
			handle.close();
			return false;
		}

		numlumps += infonumlumps;
		lumpinfo = new LumpInfo[infonumlumps];
		
		handle.seek(infotableofs, Whence.Set);

		for (int i=startlump; i < numlumps; i++)
		{
			handle.read(data, 0, 16); 
			
			int filepos = LittleEndian.getInt(data);
			int size = LittleEndian.getInt(data, 4);
			String name = new String(data, 8, 8);
			lumpinfo[i] = new LumpInfo();

			lumpinfo[i].handle = handle;
			lumpinfo[i].position = filepos;
			lumpinfo[i].size = size;
			lumpinfo[i].name = name;
		}

		return true;
	}
	
	/*
	====================
	=
	= RTS_Init
	=
	= Files with a .rts extension are idlink files with multiple lumps
	=
	====================
	*/
	
	public static void RTS_Init (String filename)
	{
	   //
	   // open all the files, load headers, and count lumps
	   //
	   numlumps = 0;
	   lumpinfo = null;   // will be realloced as lumps are added

	   Console.Println("RTS Manager Started.");
	   if (!RTS_AddFile (filename)) return;

	   if (numlumps == 0) return;

	   //
	   // set up caching
	   //
	   lumpcache = new byte[numlumps][];
	   RTS_Started = true;
	}
	
	/*
	====================
	=
	= RTS_NumSounds
	=
	====================
	*/
	
	public static int RTS_NumSounds ()
	{
		return numlumps-1;
	}
	
	/*
	====================
	=
	= RTS_SoundLength
	=
	= Returns the buffer size needed to load the given lump
	=
	====================
	*/

	public static int RTS_SoundLength (int lump)
	{
		lump++;
		if (lump >= numlumps)
			game.dassert("RTS_SoundLength: " + lump + " >= numlumps");
		return lumpinfo[lump].size;
	}
	
	/*
	====================
	=
	= RTS_GetSoundName
	=
	====================
	*/

	public static String RTS_GetSoundName (int i)
	{
		i++;
		if (i>=numlumps)
			game.dassert("RTS_GetSoundName: " + i + " >= numlumps");
	   return lumpinfo[i].name;
	}
	
	/*
	====================
	=
	= RTS_ReadLump
	=
	= Loads the lump into the given buffer, which must be >= RTS_SoundLength()
	=
	====================
	*/
	
	public static void RTS_ReadLump (int lump, byte[] dest)
	{
		if (lump >= numlumps)
			game.dassert("RTS_ReadLump: " + lump + " >= numlumps");
		if (lump < 0)
			game.dassert("RTS_ReadLump: " + lump + " < 0");
		LumpInfo l = lumpinfo[lump];
		
		l.handle.seek(l.position, Whence.Set);
		l.handle.read(dest, 0, l.size);
	}
	
	/*
	====================
	=
	= RTS_GetSound
	=
	====================
	*/
	public static byte[] RTS_GetSound (int lump)
	{
		lump++;
		if (lump >= numlumps)
			game.dassert("RTS_GetSound: " + lump + " >= " + numlumps);
	   
		if(lumpcache == null)
			game.dassert("RTS_GetSound: lumpcache == null");

		if (lumpcache[lump] == null)
		{
			lumplockbyte[lump] = (byte) 200;
			lumpcache[lump] = new byte[RTS_SoundLength(lump-1)];
			RTS_ReadLump(lump, lumpcache[lump]);
		}
		else
		{
			if ((lumplockbyte[lump] & 0xFF) < 200)
				lumplockbyte[lump] = (byte) 200;
			else
				lumplockbyte[lump]++;
		}
	   
		return lumpcache[lump];
	}

}
