import java.util.HashMap;
import java.util.Random;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
public class Markov
{
	//A bi-gram prefix engine
	//takes in a filename
	//outputs a markov chain based on it
	public static void main(String[] args)
	{
		String filename = args[0];
		HashMap<String, MarkovLink> chain = readFile(filename);
		//System.out.println(printChain(chain));
		System.out.println(generateText(chain));
	}

	public static HashMap<String, MarkovLink> readFile(String filename)
	{
		File file = new File(filename);
		String line;
		String[] words;
		FixedLengthQueue<String> queue = new FixedLengthQueue<String>(3);
		HashMap<String, MarkovLink> chain = new HashMap<String, MarkovLink>(64);
		queue.add("");
		queue.add("");
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
			while((line = br.readLine())!=null)
			{
				words = line.split("\\s+");
				for(int i = 0; i < words.length; i++)
				{
					queue.add(words[i]);
					String key = queue.get(0)+" "+queue.get(1);
					if(chain.containsKey(key))
					{
						chain.get(key).addSuffix(queue.get(2));
					}
					else
					{
						MarkovLink link = new MarkovLink(queue.get(0), queue.get(1));
						link.addSuffix(queue.get(2));
						chain.put(key, link);
					}
				}
			}
			queue.add("");
			MarkovLink link = new MarkovLink(queue.get(0), queue.get(1));
			link.addSuffix(queue.get(2));
			//System.out.println("LAST ENTRY: "+queue.get(0)+" "+queue.get(1)+" : "+queue.get(2));
			chain.put(queue.get(0)+" "+queue.get(1), link);
		}
		catch(Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		return chain;
	}

	public static String generateText(HashMap<String, MarkovLink> chain)
	{
		FixedLengthQueue<String> queue = new FixedLengthQueue<String>(3);
		queue.add("");
		queue.add("");
		String output = "";
		queue.add(chain.get(" ").getChoiceArray()[0]);
		while(!queue.get(2).equals(""))
		{
			if(output.equals(""))
			{
				output = queue.get(2);
			}
			else
			{
				output = output + " " + queue.get(2);
			}
			//System.out.println(output);
			String next = makeChoice(chain.get(queue.get(1)+" "+queue.get(2)).getChoiceArray());
			queue.add(next);
		}
		return output;
	}

	public static String makeChoice(String[] choices)
	{
		Random random = new Random();
		if(choices.length == 0)
		{
			return "";
		}
		int index = random.nextInt(choices.length);
		return choices[index];
	}

	public static String printChain(HashMap<String, MarkovLink> chain)
	{
		MarkovLink[] links = chain.values().toArray(new MarkovLink[chain.size()]);
		String out = "";
		for(MarkovLink l: links)
		{
			out = out + l.toString() + "\n";
		}
		return out;
	}
}