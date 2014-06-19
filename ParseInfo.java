//package com.simon.crawl;

import java.util.List;
import java.util.Queue;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.io.IOException;

import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class ParseInfo {
	
	private static WebDriver driver = null; 

	private static WebElement we = null; // one element.
	private static WebElement we1 = null;
	private static List<WebElement> wes = null; // multiple elements.
	
	private static String label = null;
	private static String txt = null;
	
	private static BufferedWriter out = null;
	
	private static final String PLACE_HOLDER = "NONE";

	
	// load the friends list into a queue.
	public Queue<String> load(String path) {
		return null;
	}
	
	/**
	 * Translate the xml string to special string.
	 * @param str
	 * @return
	 */
	private static String cleanTxt(String str) {
		String txt = ""; 
		
		txt = str.replaceAll("<[^>]*>", " ").trim();
		txt = txt.replace("&", "and"); 
		txt = txt.replaceAll("[\"\'\\><]", "");
			
		return txt; 
	}
	
	public static void parseAbout() throws IOException {
	
		// About
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_info']"));
		} catch (Exception e) {
			return ;
		}

		out.write("<about>");
		//// Work and education
		try {
			we1 = we.findElement(By.xpath("//div[@id='eduwork']"));		
			wes = we.findElements(By.xpath("//div[@id='eduwork']/table/tbody"));

			out.write("<edu_work>");

			for (int i = 1; i <= wes.size(); i++) {
				try {
					// the group label such as "graduate school", is hidden, so a normal xpath search can't find it. 
					we1 = we.findElement(By.xpath("//div[@id='pagelet_eduwork']/div/table/tbody[" + i + "]/tr[1]/th"));
					label = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", we1); 
					label = label.replaceAll("<[^>]*>", " ").trim().toLowerCase().replace(' ', '_');
					
					we1 = we.findElement(By.xpath("//div[@id='pagelet_eduwork']/div/table/tbody[" + i + "]/tr[1]/td"));
					
					if (we1 == null) txt = ""; 
					// txt = we1.getText().replaceAll("<[^>]*>", " ").trim(); 
					txt = cleanTxt(we1.getText());
					
					out.write("<" + label + ">");
					out.write(txt);
					out.write("</" + label + ">");
				} catch (Exception e) {
					System.err.println("Error parsing education and work items.");
				}
			}
			out.write("</edu_work>"); 
			
		} catch (Exception e) {
			System.err.println("Error parsing education and work.");
		}

		//// Relationship
		try {
			we1 = we.findElement(By.xpath("//div[@id='relationships']"));
			out.write("<relationships>");
			
			try {
				we1 = we.findElement(By.xpath("//div[@id='relationships']/table/tbody/tr/td/div/div/div/div[2]"));
				txt = cleanTxt(we1.getText()); //.replaceAll("<[^>]*>", " ");
				out.write(txt);
			} catch (Exception e) { }
			
			out.write("</relationships>");
			
		} catch (Exception e) {
			System.err.println("Error parsing relationships.");
		}

		//// Family
		try {
			we1 = we.findElement(By.xpath("//div[@id='family']"));

			out.write("<family>");
				
			wes = we.findElements(By.xpath("//div[@id='family-relationships-pagelet']/div/ul/li"));
			
			for ( int i = 1; i <= wes.size(); i++) {
				//*[@id="family-relationships-pagelet"]/div/ul/li[1]/div/div/div/div[2]/div[2]
				//*[@id="family-relationships-pagelet"]/div/ul/li/
				try {
					we1 = we.findElement(By.xpath("//div[@id='family-relationships-pagelet']/div/ul/li[" + i + "]/div/div/div/div[2]/div[2]"));
					label = we1.getText().toLowerCase().replace(' ', '_').replaceAll("[()]", "");
					if(label.equals("")) label = "member";
				} catch (Exception e) {
					label = "member";
				}
		
				try {
					we1 = we.findElement(By.xpath("//div[@id='family-relationships-pagelet']/div/ul/li[" + i + "]/div/div/div/div[2]/div[1]")); 
					txt = cleanTxt(we1.getText());
				} catch (Exception e) {
					txt = PLACE_HOLDER; 
				}
				
				out.write("<" + label + ">");
				out.write(txt);
				out.write("</" + label + ">");
			}
						
			out.write("</family>");

		} catch (Exception e) {
			System.err.println("Error parsing family.");
		}
		
		//// pages --> fuady.hidayat
		try {
			we1 = we.findElement(By.xpath("//*[@id='pagelet_featured_pages']/div/div/div/div/h4"));

			out.write("<pages>");
			try {//*[@id="pagelet_featured_pages"]/div/table/tbody/tr/td/div/div/div/div[2]/div/a
				wes = we.findElements(By.xpath("//div[@id='pagelet_featured_pages']/div/table/tbody"));

				txt = "";
				for ( int i = 1; i <= wes.size(); i++) {
					we1 = we.findElement(By.xpath("//div[@id='pagelet_featured_pages']/div/table/tbody[" + i + "]/tr/td/div/div/div/div[2]/div/a"));
					txt += "<page>" + cleanTxt(we1.getText()) + "</page>";
				}
				
				out.write(cleanTxt(txt));
			} catch (Exception e) {}
		
			out.write("</pages>");

		} catch (Exception e) {
			System.err.println("Error parsing pages.");
		}

		//// About xxx
		try {
			//*[@id="pagelet_bio"]/div/div[1]/div/div[2]/h4 --> for login user.
			//*[@id="pagelet_bio"]/div/div[1]/div/div/h4    --> for common user. 
			we1 = we.findElement(By.xpath("//div[@id='pagelet_bio']/div/div[1]/div/div/h4"));
			label = we1.getText().toLowerCase().replace(' ', '_');
			txt = we.findElement(By.xpath("//div[@id='pagelet_bio']/div/div[2]")).getText();
			txt = cleanTxt(txt);
			
			out.write("<" + label + ">");
			out.write(txt.equals("") ? PLACE_HOLDER : txt);
			out.write("</" + label + ">");
	
		} catch (Exception e) {
			System.err.println("Error parsing about xxx.");
		}

		//// Favorite Quotations
		try {
			we1 = we.findElement(By.xpath("//div[@id='pagelet_quotes']/div/div[1]/div/div/h4"));
			
			label = we1.getText().toLowerCase().replace(' ', '_');

			txt = we.findElement(By.xpath("//div[@id='pagelet_quotes']/div/div[2]")).getText();
			txt = cleanTxt(txt);
			
			out.write("<" + label + ">");			
			out.write(txt.equals("") ? PLACE_HOLDER : txt);
			out.write("</" + label + ">");
		} catch (Exception e) {
			System.err.println("Error parsing favorite quotations.");
		}

		//// Living
		try {
			we1 = we.findElement(By.xpath("//div[@id='pagelet_hometown']/div/div/div/div/h4"));
			out.write("<living>");
			////// current city
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_hometown']/div/table/tbody/tr[1]/td/div/div/div/div[2]/div[2]"));
				label = we1.getText().toLowerCase().replace(' ', '_');
				
				we1 = we.findElement(By.xpath("//*[@id='pagelet_hometown']/div/table/tbody/tr[1]/td/div/div/div/div[2]/div[1]/a"));
				txt = cleanTxt(we1.getText());

				out.write("<" + label + ">");
				out.write(txt);
				out.write("</" + label + ">");
			} catch (Exception e) {
				System.err.println("Error parsing current city.");
			}
			
			////// Hometown
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_hometown']/div/table/tbody/tr[2]/td/div/div/div/div[2]/div[2]"));
				label = we1.getText().toLowerCase().replace(' ', '_');

				we1 = we.findElement(By
						.xpath("//*[@id='pagelet_hometown']/div/table/tbody/tr[2]/td/div/div/div/div[2]/div[1]/a"));
				txt = cleanTxt(we1.getText());

				out.write("<" + label + ">");
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
				out.write("</" + label + ">");
				
			} catch (Exception e) {
				System.err.println("Error parsing hometown.");
			}

			out.write("</living>");
			
		} catch (Exception e) {
			System.err.println("Error parsing living.");
		}

		//// Basic Information
		try {//*[@id="pagelet_basic"]/div/div/div/div/h4
			
			we1 = we.findElement(By.xpath("//div[@id='pagelet_basic']/div/div/div/div/h4"));
			out.write("<basic_info>");
			////// Birthday
			////// gender
			////// interested_in (men, women, both)
			////// Relationship_status
			////// anniversary
			////// languages
			////// Religious views
			////// political views

			wes = we.findElements(By.xpath("//div[@id='pagelet_basic']/div/table/tbody"));

			for (int i = 1; i <= wes.size(); ++i) {

				try {
					we1 = we.findElement(By.xpath("//*[@id='pagelet_basic']/div/table/tbody[" + i + "]/tr/th"));
					label = we1.getText().toLowerCase().replace(' ', '_');
	
					if (label.equals("")) continue;
					we1 = we.findElement(By.xpath("//*[@id='pagelet_basic']/div/table/tbody[" + i + "]/tr/td"));
					txt = cleanTxt(we1.getText());
	
					out.write("<" + label + ">");
					out.write(txt.equals("") ? PLACE_HOLDER : txt);
					out.write("</" + label + ">");
				} catch (Exception e) {	}
				
				// handle a special case. 
				try {
					we1 = we.findElement(By.xpath("//*[@id='pagelet_basic']/div/table/tbody[" + i + "]/tr[2]/th"));
					label = we1.getText().toLowerCase().replace(' ', '_');

					we1 = we.findElement(By.xpath("//*[@id='pagelet_basic']/div/table/tbody[" + i + "]/tr[2]/td"));
					txt = cleanTxt(we1.getText());
				
					out.write("<" + label + ">");
					out.write(txt.equals("") ? PLACE_HOLDER : txt);
					out.write("</" + label + ">");
				
				} catch (Exception e) {
					System.err.println("Error handling the special case. ");
				}
			}
			
			out.write("</basic_info>");
			
		} catch (Exception e) {
			System.err.println("Error parsing basic information.");
		}

		//// Contact Information
		try {
			we1 = we.findElement(By.xpath("//div[@id='pagelet_contact']/div/div/div/div/div/h4"));
			out.write("<contact_info>");
			////// mobile phone
			////// Other phones
			////// Address
			////// neighborhood
			////// Screen Names
			////// Website
			////// email
			////// Facebook (URI)
			wes = we.findElements(By.xpath("//div[@id='pagelet_contact']/div/div/table/tbody"));

			for (int i = 1; i <= wes.size(); ++i) {
				try {
					we1 = we.findElement(By.xpath("//div[@id='pagelet_contact']/div/div/table/tbody[" + i + "]/tr/th"));
					label = we1.getText().toLowerCase().replace(' ', '_').trim();
	
					if (label.equals("")) continue;
					we1 = we.findElement(By.xpath("//div[@id='pagelet_contact']/div/div/table/tbody[" + i + "]/tr/td"));
					txt = cleanTxt(we1.getText());
	
					out.write("<" + label + ">");
					out.write(txt.equals("") ? PLACE_HOLDER : txt);
					out.write("</" + label + ">");
				} catch (Exception e) {	}
			}

			out.write("</contact_info>");
			
		} catch (Exception e) {
			System.err.println("Error parsing contact information.");
		}

		//// History by Year
		try {
			we1 = we.findElement(By.xpath("//div[@id='pagelet_yearly']/div/div[1]/div/div/h4"));
			out.write("<history_by_year>");

			// *[@id="pagelet_yearly"]/div/div[2]/table/tbody
			wes = we.findElements(By.xpath("//div[@id='pagelet_yearly']/div/div[2]/table/tbody/tr"));

			for (int i = 1; i <= wes.size(); ++i) {
				// for(WebElement w : wes) {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_yearly']/div/div[2]/table/tbody/tr[" + i + "]/td[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replaceAll("<[^>]*>", " ").trim();

				if (label.equals("")) continue;
				if (!Pattern.compile("[a-zA-Z]").matcher(label).find())
					label = "_" + label;

				we1 = we.findElement(By.xpath("//div[@id='pagelet_yearly']/div/div[2]/table/tbody/tr[" + i + "]/td[2]"));
				txt = cleanTxt(we1.getText());

				out.write("<" + label + ">");
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
				out.write("</" + label + ">");
			}
			out.write("</history_by_year>");

		} catch (Exception e) {
			System.err.println("Error parsing history by year.");
		}
		out.write("</about>");
	}
	
	public static void parseFriends() throws IOException {
		// Friends //*[@id="pagelet_timeline_medley_friends"]/div[1]/div[1]
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_friends']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
		
		out.write("<friends>");
		//// All Friends
		//// Mutual Friends
		//// Recently Added
		//// Following
		//// Followers
		///// People you may know -->> suggest friends from a person.
		///// High School -->> Friend grouping information
		///// College -->> Friend grouping information

		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_friends']/div[1]/div[2]/div[1]/a"));
		
		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_friends']/div[1]/div[2]/div[1]/a[" + i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replaceAll("<[^>]*>", " ").replace('\'', '_').trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_friends']/div[1]/div[2]/div[1]/a["
								+ i + "]/span[2]"));
				txt = cleanTxt(we1.getText());
	
				if (label.equals("")) continue;
				out.write("<" + label + ">");
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
				out.write("</" + label + ">");
			} catch (Exception e) {} 
		}

		out.write("</friends>");

	}
	
	public static void parsePhotos() throws IOException {
		// Photos
		//// Photos of xxx (Photos that xxx was tagged and/or shared by
		// others.)
		//// xxx's Photos (Photos uploaded by xxx. )
		//// Albums (xxx's Albums)
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_photos']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
		
		out.write("<photos>");
		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_photos']/div[1]/div[2]/div[1]/a"));
		
		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_photos']/div[1]/div[2]/div[1]/a["	+ i + "]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replaceAll("<[^>]*>", " ").replace('\'', '_').trim();
				
				if (label.equals("") || label.length() <= 1)
					continue;
		
				out.write("<" + label + ">");
				out.write(PLACE_HOLDER);
				out.write("</" + label + ">");
			} catch (Exception e) {}
		}
		
		out.write("</photos>");
	}
	
	public static void parseMovies() throws IOException {
		// Movies
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_movies']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
		out.write("<movies>");
		//// Watched
		//// Want to Watch
		//// Likes

		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_movies']/div[1]/div[2]/div[1]/a"));
		
		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_movies']/div[1]/div[2]/div[1]/a["	+ i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').replaceAll("<[^>]*>", " ").trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_movies']/div[1]/div[2]/div[1]/a[" + i + "]/span[2]"));
				txt = cleanTxt(we1.getText());
			} catch (Exception e) { continue; }
			
			if (label.equals("")) continue;
			out.write("<" + label + ">");
			
			// get details for the first tab.
			if(1 == i) {
				wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_movies']/div[2]/div/ul/li"));
				for (int j = 1; j <= wes.size(); j++) {
					try {
						we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_movies']/div[2]/div/ul/li[" + j + "]/div/div/a"));
						txt = cleanTxt(we1.getText());
						
						we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_movies']/div[2]/div/ul/li[" + j + "]/div/div/div"));
						String type = cleanTxt(we1.getText());
						
						out.write("<like type=\"" + type + "\">");
						out.write(txt);
						out.write("</like>");
					} catch (Exception e) {
						System.err.println("Error parsing movie item.");
					}
				}
			} else {
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
			}
			
			out.write("</" + label + ">");
		}

		out.write("</movies>");
	}

	
	public static void parseTVshows() throws IOException {
		// TVshows
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_tv']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
		
		out.write("<tvshows>");
		//// Likes
		//// Watched
		//// Want to Watch

		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_tv']/div[1]/div[2]/div[1]/a"));

		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_tv']/div[1]/div[2]/div[1]/a["	+ i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').replaceAll("<[^>]*>", " ").trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_tv']/div[1]/div[2]/div[1]/a["	+ i + "]/span[2]"));
				txt = cleanTxt(we1.getText());
			} catch (Exception e) { continue; }
			
			if (label.equals("")) continue;
			out.write("<" + label + ">");
			
			// get details for the first tab.
			if(1 == i) {
				
				wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_tv']/div[2]/div/ul/li"));
				for (int j = 1; j <= wes.size(); j++) {
					try {
						we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_tv']/div[2]/div/ul/li[" + j + "]/div/div/a"));
						txt = cleanTxt(we1.getText());
						
						we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_tv']/div[2]/div/ul/li[" + j + "]/div/div/div"));
						String type = cleanTxt(we1.getText());
						
						out.write("<like type=\"" + type + "\">");
						out.write(txt);
						out.write("</like>");
					} catch (Exception e) {
						System.err.println("Error parsing tv show item.");
					}
				}
			
			} else {
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
			}
			
			out.write("</" + label + ">");
		}
		out.write("</tvshows>");
	}
	
	public static void parseMusic() throws IOException {
		// Music //*[@id="pagelet_timeline_medley_music"]/div[1]/div[1]
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_music']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
			
		out.write("<musics>");

		//// Likes
		//// Listen Later
		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_music']/div[1]/div[2]/div[1]/a"));
		
		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_music']/div[1]/div[2]/div[1]/a[" + i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').replaceAll("<[^>]*>", " ").trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_music']/div[1]/div[2]/div[1]/a[" + i + "]/span[2]"));
				txt = cleanTxt(we1.getText());
			} catch (Exception e) { continue; }
			
			if (label.equals("")) continue;
			out.write("<" + label + ">");
			
			// get details for the first tab.
			if(1 == i) {
				
					wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_music']/div[2]/div/ul/li"));
					for (int j = 1; j <= wes.size(); j++) {
						try {
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_music']/div[2]/div/ul/li[" + j + "]/div/div[2]/a"));
							txt = cleanTxt(we1.getText());
							
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_music']/div[2]/div/ul/li[" + j + "]/div/div[2]/div"));
							String type = cleanTxt(we1.getText());
							
							out.write("<like type=\"" + type + "\">");
							out.write(txt);
							out.write("</like>");
						} catch (Exception e) {
							System.err.println("Error parsing music item.");
						}
					}
				
			} else {
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
			}
			
			out.write("</" + label + ">");
		}

		out.write("</musics>");
	}
	
	public static void parseLikes() throws IOException {
		// Likes
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_likes']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
		out.write("<likes>");
		//// Interests
		//// Foods
		//// Restaurants
		//// Activities
		//// Websites
		//// Athletes
		//// Sports Team
		//// Sports
		//// Inspirational People
		//// Clothing
		//// Other Likes

		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_likes']/div[1]/div[2]/div[1]/a"));

		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_likes']/div[1]/div[2]/div[1]/a[" + i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').replaceAll("<[^>]*>", " ").trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_likes']/div[1]/div[2]/div[1]/a[" + i + "]/span[2]"));
				txt = we1.getText().replaceAll("<[^>]*>", " ");
			} catch (Exception e) { continue; }
			if (label.equals("")) continue;
			out.write("<" + label + ">");

			// get details for the first tab.
			if(1 == i) {
					wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_likes']/div[2]/div/ul/li"));
					for (int j = 1; j <= wes.size(); j++) {
						try {
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_likes']/div[2]/div/ul/li[" + j + "]"));
							txt = cleanTxt(we1.getText());
							
							out.write("<like>");
							out.write(txt);
							out.write("</like>");
						} catch (Exception e) {
							System.err.println("Error parsing music item.");
						}	
					}				
			} else {
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
			}
			
			out.write("</" + label + ">");
		}
		out.write("</likes>");
	}
	
	public static void parsePlaces() throws IOException {
		// Places
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_map']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return ;	
		}
		out.write("<places>");
		//// All places
		//// Life Events
		//// Photos

		//*[@id="pagelet_timeline_medley_map"]/div[1]/div[2]
		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_map']/div[1]/div[2]/div[1]/span/a"));

		//System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX" + cnt);
		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_map']/div[1]/div[2]/div[1]/span/a[" + i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').trim();
				
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_map']/div[1]/div[2]/div[1]/span/a[" + i + "]/span[2]"));
				txt = cleanTxt(we1.getText());
			} catch (Exception e) { continue; }
			
			if (label.equals("")) continue;
			out.write("<" + label + ">");
			out.write(txt.equals("") ? PLACE_HOLDER : txt);
			out.write("</" + label + ">");
		}

		out.write("</places>");
	}
	
	public static void parseBooks() throws IOException {
		// Books
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_movies']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
		out.write("<books>");
		//// Read
		//// Want to Read
		//// Likes
		wes = we.findElements(By
				.xpath("//div[@id='pagelet_timeline_medley_books']/div[1]/div[2]/div[1]/a"));

		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_books']/div[1]/div[2]/div[1]/a[" + i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').replaceAll("<[^>]*>", " ").trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_books']/div[1]/div[2]/div[1]/a[" + i + "]/span[2]"));
				txt = cleanTxt(we1.getText());
			} catch (Exception e) { continue; }
			
			if (label.equals("")) continue;
			out.write("<" + label + ">");
			
			// get details for the first tab.
			if(1 == i) {
				
					wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_books']/div[2]/div/ul/li"));
					for (int j = 1; j <= wes.size(); j++) {
						try {
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_books']/div[2]/div/ul/li[" + j + "]/div/div/a"));
							txt = cleanTxt(we1.getText());
							
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_books']/div[2]/div/ul/li[" + j + "]/div/div/div"));
							String type = cleanTxt(we1.getText());
							
							out.write("<like type=\"" + type + "\">");
							out.write(txt);
							out.write("</like>");
						} catch (Exception e) {
							System.err.println("Error parsing book item.");
						}
					}
				
			} else {
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
			}

			out.write("</" + label + ">");
		}
		out.write("</books>");
	}
	
	public static void parseGames() throws IOException {
		// Games
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_games']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
		out.write("<games>");
		//// Recent Games
		//// Likes

		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_games']/div[1]/div[2]/div[1]/a"));

		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_games']/div[1]/div[2]/div[1]/a[" + i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').replaceAll("<[^>]*>", " ").trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_games']/div[1]/div[2]/div[1]/a[" + i + "]/span[2]"));
				txt = cleanTxt(we1.getText());
			} catch (Exception e) { continue; }				

			if (label.equals("")) continue;
			out.write("<" + label + ">");
			
			// get details for the first tab.
			if(1 == i) {
				
					wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_games']/div[2]/div/ul/li"));
					for (int j = 1; j <= wes.size(); j++) {
						try {
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_games']/div[2]/div/ul/li[" + j + "]/div/div/a"));
							txt = cleanTxt(we1.getText());
							
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_games']/div[2]/div/ul/li[" + j + "]/div/div/div"));
							String type = cleanTxt(we1.getText());
							
							out.write("<like type=\"" + type + "\">");
							out.write(txt);
							out.write("</like>");
						} catch (Exception e) {
							System.err.println("Error parsing game item.");
						}		
					}
			} else {
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
			}

			out.write("</" + label + ">");
		}
		out.write("</games>");
	}
	
	public static void parseEvents() throws IOException {
		// Events
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_events']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
			
		out.write("<events>");
		//// Upcoming
		//// Past

		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_events']/div[1]/div[2]/div[1]/a"));

		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_events']/div[1]/div[2]/div[1]/a[" + i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').replaceAll("<[^>]*>", " ").trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_events']/div[1]/div[2]/div[1]/a["	+ i + "]/span[2]"));
				txt = we1.getText().replaceAll("<[^>]*>", " ");
			} catch (Exception e) { continue; }
			
			if (label.equals("")) continue;
			out.write("<" + label + ">");
			
			// get details for the first tab.
			if(1 == i) {
				
					wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_events']/div[2]/div/ul/li"));
					for (int j = 1; j <= wes.size(); j++) {
						try {
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_events']/div[2]/div/ul/li[" + j + "]/div/div/div[2]/a"));
							txt = cleanTxt(we1.getText());
							
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_events']/div[2]/div/ul/li[" + j + "]/div/div/div[2]/div"));
							String type = cleanTxt(we1.getText());
							
							out.write("<event desc=\"" + type + "\">");
							out.write(txt);
							out.write("</event>");
						} catch (Exception e) {
							System.err.println("Error parsing game item.");
						}
					}

			} else {
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
			}
			
			out.write("</" + label + ">");
		}
		out.write("</events>");
	}
	
	public static void parseGroups() throws IOException {
		// Groups
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_groups']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
		out.write("<groups>");
		//// Open
		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_groups']/div[1]/div[2]/div[1]/a"));

		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_groups']/div[1]/div[2]/div[1]/a[" + i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').replaceAll("<[^>]*>", " ").trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_groups']/div[1]/div[2]/div[1]/a[" + i + "]/span[2]"));
				txt = cleanTxt(we1.getText());
			} catch (Exception e) { continue; }
			
			if (label.equals("")) continue;
			out.write("<" + label + ">");
			
			// get details for the first tab.
			if(1 == i) {
				
					wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_groups']/div[2]/div/ul/li"));
					for (int j = 1; j <= wes.size(); j++) {
						try {
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_groups']/div[2]/div/ul/li[" + j + "]/div/div/div[2]/div[2]/div"));
							txt = cleanTxt(we1.getText());
							
							we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_groups']/div[2]/div/ul/li[" + j + "]/div/div/div[2]/div[2]/div[2]"));
							String desc = cleanTxt(we1.getText());
							
							out.write("<group name=\"" + txt + "\" desc=\"" + desc + "\">");
							out.write(txt);
							out.write("</group>");
						} catch (Exception e) {
							System.err.println("Error parsing group item.");
						}
					}

			} else {
				out.write(txt.equals("") ? PLACE_HOLDER : txt);
			}
			
			out.write("</" + label + ">");
		}
		out.write("</groups>");
	}
	
	
	public static void parseNotes() throws IOException {
		// Notes
		try {
			we = driver.findElement(By.xpath("//div[@id='pagelet_timeline_medley_notes']/div[1]/div[1]/h3"));
		} catch (Exception e) {
			return; 
		}
		out.write("<notes>");
			//// Notes
			//// Drafts

		wes = we.findElements(By.xpath("//div[@id='pagelet_timeline_medley_notes']/div[1]/div[2]/div[1]/a"));

		for (int i = 1; i <= wes.size(); ++i) {
			try {
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_notes']/div[1]/div[2]/div[1]/a[" + i + "]/span[1]"));
				label = we1.getText().toLowerCase().replace(' ', '_').replace('\'', '_').replaceAll("<[^>]*>", " ").trim();
	
				we1 = we.findElement(By.xpath("//div[@id='pagelet_timeline_medley_notes']/div[1]/div[2]/div[1]/a[" + i + "]/span[2]"));
				txt = cleanTxt(we1.getText());
			} catch (Exception e) { continue; }
			
			if (label.equals("")) continue;
			out.write("<" + label + ">");
			out.write(txt.equals("") ? PLACE_HOLDER : txt);
			out.write("</" + label + ">");
		}
		out.write("</notes>");
	}

    public static void DEBUG(String info) {
	System.out.println("DEBUG: " + info); 
    }

    public static void INFO(String info) {
	System.out.println("INFO: " + info); 
    }
	
	// the main
	public static void main(String[] args) throws Exception {
	    DEBUG("command line length: " + args.length);
		if(args.length != 2) {
		    System.err.println("Usage: ParseInfo <about_dir> <output_dir>");
		    System.exit(1); 
		}

		INFO("About directory -- " + args[0]);
		INFO("Output directory -- " + args[1]);
		// System.exit(0); 

		String aboutDir = args[0].endsWith("/") ? args[0] : args[0] + "/"; 
		String xmlDir = args[1].endsWith("/") ? args[1] : args[1] + "/"; 

		FirefoxProfile profile = new FirefoxProfile();
		profile.setEnableNativeEvents(true);
		driver = new FirefoxDriver(profile);
		driver.manage().window().maximize();
		
		File dir = new File(aboutDir);
		File[] files = dir.listFiles(); 
		
		for (File file : files) {
			System.out.println("Processing file: " + file.getName()); 
			String name = "file:///" + file.getAbsolutePath(); 
			if(name.indexOf(".about.html") <= 0) continue; 
			
			String xmlFileName = xmlDir + file.getName().replaceAll(".html", ".xml");

			File f = new File(xmlFileName); 
			if(f.exists()) continue; 

			driver.get(name);

			// scroll to the end.
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
			
			FileWriter fstream = new FileWriter(xmlFileName);
			out = new BufferedWriter(fstream);
			out.write("<profile>");
			
			// output the user identifier. 
			String uid = file.getName().substring(0, file.getName().indexOf(".about.html")); 
			System.out.println("Parsing info for user: " + uid); 

			out.write("<user>" + uid + "</user>"); 
			try {
				parseAbout(); 
				parseFriends(); 
				parsePhotos(); 
				parseMovies();
				parseTVshows(); 
				parseMusic(); 
				parseLikes(); 
				parsePlaces(); 
				parseBooks(); 
				parseGames(); 
				parseEvents(); 
				parseGroups(); 
				parseNotes(); 
				// TODO
				// Timeline
				//// posts //*[@class="_4_7u"]/ol/li[1] exists.
			} catch (Exception e) { 
				System.err.println("Error parsing profile.");
			}
			out.write("</profile>");
			out.close();
		}
 	}
}
