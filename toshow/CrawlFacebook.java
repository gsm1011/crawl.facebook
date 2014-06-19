//package com.simon.crawl;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Random;

import java.io.File;
import java.io.Console;
import java.io.FileWriter; 
import java.io.BufferedWriter; 
import java.io.PrintWriter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.Charset; 

import java.util.regex.Matcher; 
import java.util.regex.Pattern; 

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class CrawlFacebook {
	public static WebDriver driver = null;
	public static String FRIENDS_DIR_BASE = "./friends.";
	private static String friendsDir = null;
	public static String loginId = null;

	public static boolean crawlFriends = false; 
	// load the friends list into a queue. 
    public static LinkedList<String> load() {
    	LinkedList<String> queue = new LinkedList<String>(); 
    	
    	assert(!(friendsDir == null || friendsDir.equals("")));
    	
    	// load all existing data into queue. 
		File dir = new File(friendsDir);
		if(!dir.exists()) {
			// if friends page doesn't exist, make it and exit. 
			dir.mkdir();
			return queue; 
		}
		
		File[] files = dir.listFiles();
		
		if(files == null) return queue; 
		
		List<String> lines = null; 
		
		for (File file : files) {
			
		    if(file.getName().endsWith(".txt") && file.isFile() && file.length() > 0) {
		    	
				try {
				    lines = Files.readAllLines(Paths.get(file.getAbsolutePath()), Charset.forName("UTF-8")); 
				} catch (Exception e) { continue; }
				
				for(String line : lines) {
				    if(line == null || line.equals("")) continue; 
				    String [] ps = line.split(",");
				    if(ps.length < 2) continue; // broken lines
				    queue.add(ps[0]); // add the user id into the queue. 
				}
		    }
		}

		return queue; 
    }

    // used for writing data to files. 
    private static FileWriter fileWriter = null; 
    private static BufferedWriter bufferedWriter = null;
    
    // used to operate on files and directories. 
    private static File file = null; 
    private static File directory = null;
    private static String filename = null; // the file name to store a web page as. 
    
    /**
     * The generic method to crawl and save a web page. 
     * @param uid The user id to crawl. 
     * @param part Which part to crawl, candidates include: timeline, about, etc.
     * @param saveDir The directory to save the web page to. 
     * @return void.
     */
    private static void crawlAndSave(String uid, String part, String saveDir) {
    	assert(driver != null);
    	assert(!(uid.equals("") || uid == null)); 
    	assert(!(saveDir.equals("") || saveDir == null));
    	
    	System.out.println("Crawling " + part + " for user " + uid); 

    	// make sure the save directory exists. 
    	directory = new File(saveDir); 
    	if(!directory.exists()) directory.mkdir(); 
    	
    	filename = saveDir + "/" + uid + "." + (part.equals("") ? "timeline" : part) + ".html";
    	
    	file = new File(filename);
    	if(file.exists()) return;  
    	
    	if(Pattern.compile("[a-zA-Z]").matcher(uid).find()) {
    		driver.get("https://www.facebook.com/" + uid + "/" + part);
		} else {
			driver.get("https://www.facebook.com/profile.php?id=" + 
			uid + (part.equals("") ? "" : "&sk=" + part));
		}
    		
		try {
			// scroll to the end. 
			scrollToBottom(driver);
	
			// click the button to show older stories for timeline.  
			driver.findElement(By.xpath("//div[@id='pagelet_timeline_recent']/div[2]/div/a")).click();
			
			scrollToBottom(driver);
		} catch (Exception e) {}
		
		try {
			// click the "See All" button on the About part.
			driver.findElement(By.xpath("//*[@id='pagelet_timeline_medley_info']/div[2]/div/a")).click();

			scrollToBottom(driver);
		} catch (Exception e) {}
		
		// save the file. 
		try {
			fileWriter = new FileWriter(filename);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(driver.getPageSource());
			bufferedWriter.flush();
			System.out.println("Saved result to file: " + filename);
			
		} catch (Exception e) {
			// System.err.println("Error writing to file " + filename); 
		}

		try {
			Thread.sleep(new Random().nextInt(10) * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    // scroll webpage to the bottom. 
    private static void scrollToBottom(WebDriver driver) throws InterruptedException {
    	assert(driver != null);
    	
	    // scroll page to the end. 
	    while (true) {

	    	Long y = (Long) ((JavascriptExecutor) driver).executeScript("return window.pageYOffset");
	    	long y1 = y.longValue();
		
			//((JavascriptExecutor) driver).executeScript("window.scrollBy(0,500)");
			((JavascriptExecutor)driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
			Thread.sleep(1800);
			y = (Long) ((JavascriptExecutor) driver).executeScript("return window.pageYOffset");
			if (y1 - y.longValue() == 0) break;
	    }
    }

    // the main
    public static void main(String[] args) throws Exception {

		// get Username and password for facebook.		
		Scanner input = null;
		
		Console console = System.console();
		assert(console != null);
		String email = null; 
		String password = null; 
		
		File credential = new File(".fb_login_credential"); 
		if(credential.exists() && credential.length() > 0) {
			input = new Scanner(credential);
			String [] p = input.nextLine().trim().split("\t", 2);
			email = p[0].trim();
			password = p[1].trim();
			
		} else {
			input = new Scanner(System.in);
			System.out.print("Enter you facebook email (mail@example.com): ");
			email = input.nextLine();
			// Does not work in an IDE, use a console terminal instead. 
			char [] pass = console.readPassword("Enter your facebook password: "); // System.out.print("Enter you facebook password: ");
			password = new String(pass); // input.nextLine();
			
			// save username and password. 
			fileWriter = new FileWriter(".fb_login_credential"); 
			fileWriter.write(email + "\t" + password);
			fileWriter.close();
		}
		
		input.close();
		FirefoxProfile profile = new FirefoxProfile();
		profile.setEnableNativeEvents(true);
		driver = new FirefoxDriver(profile);
		driver.manage().window().maximize();
		driver.get("http://facebook.com");
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(email);
		driver.findElement(By.xpath("//input[@id='pass']")).sendKeys(password);
		driver.findElement(By.xpath("//input[@type='submit']")).click();
	
		driver.get("https://www.facebook.com");
		
		// get the login user id. 
		// String loginId = null; 
		try {
			loginId = driver.findElement(By.xpath("//a[@class='fbxWelcomeBoxName']")).getAttribute("href");
			loginId = loginId.substring("https://www.facebook.com/".length(), loginId.length()).trim();
			if(loginId == null || loginId.equals("")) throw new Exception("Can't find login id. ");
		
			// initialize the page directories with the login id.
			friendsDir = loginId + ".friends/";
			
			System.out.println("You are crawling with user: " + loginId); 
		
		} catch (Exception e) {
		    System.err.println("Login failed, please try again with the correct [username] and [password]! ");
		System.err.println(e.getMessage()); 
		    System.exit(1); 
		} 
	
		// use a file to store the friends of a user.
		// "" means the login id. 
		Queue<String> queue = null;
		String[] parts = {"about"}; // only crawl the timeline and the about page. 
		// String[] parts = {"about", "music", "movies", "tv", "books", "games", "likes", "events", "groups", "notes"}; 
//		String[] parts = {"", "about", "photos", "map", "music", "movies", "tv", "books", "games", "likes", "events", "groups", "notes"};
		
		// load all friends from file. 
		queue = load(); 
		queue.add(loginId);
		PrintWriter pwf = null;
			   
		while (queue.size() > 0) {
			
		    String uid = queue.element();
		    queue.remove();
		    if(uid.equals("")) continue; 
		    
		    // crawl the timeline and about page. 
		    for(String part : parts) {
		    	crawlAndSave(uid, part, loginId + "." + (part.equals("") ? "timeline" : part));
		    }


		    // weather to contiune to crawl the friend list. 
		    if(!crawlFriends) continue; 
		    
		    file = new File(friendsDir + uid + ".txt"); 
				
		    // test if user already been crawled. 
		    if(file.exists()) continue;
		    pwf = new PrintWriter(file);
//		    fileWriter = new FileWriter(friendsDir + uid + ".txt");
//		    bufferedWriter = new BufferedWriter(fileWriter);

		    if(Pattern.compile("[a-zA-Z]").matcher(uid).find()) {
		    	driver.get("https://www.facebook.com/" + uid + "/friends");
		    } else {
		    	driver.get("https://www.facebook.com/profile.php?id=" + uid + "&sk=friends");
		    }
	
		    scrollToBottom(driver);
		    
		    // save the friends page.
		    if(!(new File(friendsDir + uid + ".friends.html").exists())) {
			    FileWriter fwf = new FileWriter(friendsDir + uid + ".friends.html");
			    BufferedWriter bwf = new BufferedWriter(fwf);
			    bwf.write(driver.getPageSource());
			    bwf.close(); 
		    }
		    
		    List<WebElement> allNames = null; 
		    try {
		    	allNames = driver.findElements(By.xpath("//div[@class='fsl fwb fcb']/a"));
		    } catch (Exception e) {
		    	System.err.println("Sorry, user " + uid + " doesn't have visible friends! ");
		    }
		    
		    System.out.println(uid + " has " + allNames.size() + " friends.");
		    for (WebElement we : allNames) {
			try {
			    String href = we.getAttribute("href");
	
			    int startIndex = "https://www.facebook.com/".length();
			    if (href.indexOf("profile.php?id=") > 0) startIndex += "profile.php?id=".length();
					    
			    // error happens in method to find 'fref=', if it doesn't exit, just ignore the item.
			    String id = href.substring(startIndex, href.indexOf("fref=") - 1);
						
			    // add to queue to crawl its friends. 
			    queue.add(id);
						
			    String name = we.getText();
			    // bw.write(id+"," + name + "," + href.substring(0, href.indexOf("fref=") - 1) + "\n");
			    pwf.write(id + "," + name + "\n");
				
			} catch (Exception e) {	
				// System.err.println("Error getting friends: " + e.getMessage());
			}
		    }
		}
		
		if(pwf != null) pwf.close();
		if(driver != null) driver.close();
	    if(fileWriter != null) fileWriter.close();
	    if(bufferedWriter != null) bufferedWriter.close();
    }
}
