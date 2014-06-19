//package com.simon.crawl;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

import java.io.File;
import java.io.FileWriter; 
import java.io.BufferedWriter; 

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

public class FacebookFriends {
    // load the friends list into a queue. 
    public Queue<String> load(String path) {
	return null; 
    }

    // the main
    public static void main(String[] args) throws InterruptedException {
	if(args.length != 2) {
	    System.out.println(args.length); 
	    System.err.println("Usage: ./run.sh [email] [pass]");
	    return; 
	}

	String email = args[0]; 
	String password = args[1]; 

	FirefoxProfile profile = new FirefoxProfile();
	profile.setEnableNativeEvents(true);
	WebDriver driver = new FirefoxDriver(profile);
	driver.manage().window().maximize();
	driver.get("http://facebook.com");
	driver.findElement(By.xpath("//input[@id='email']")).sendKeys(email);
	driver.findElement(By.xpath("//input[@id='pass']")).sendKeys(password);
	driver.findElement(By.xpath("//input[@type='submit']")).click();

	driver.get("https://www.facebook.com");
	// get the login user id. 
	String loginId = null; 
	try {
	loginId = driver.findElement(By.xpath("//a[@class='fbxWelcomeBoxName']")).getAttribute("href");
	loginId = loginId.substring("https://www.facebook.com/".length(), loginId.length()); 
	System.out.println(loginId); 
	} catch (Exception e) {
	    System.out.println("Error!!!");
	    System.exit(1); 
	}
	// System.exit(0); 

	// use a file to store the friends of a user. 
	File f = null; 
	FileWriter fw = null; 
	BufferedWriter bw = null;

	Queue<String> queue = new LinkedList<String>();

	if (!(loginId == null || loginId == "")) queue.add(loginId);
	else { 
	    System.err.println("Error obtaining the login user id."); 
	    System.exit(0); 
	}

	// load all existing data into queue. 
	File dir = new File("./friends/");
	File[] files = dir.listFiles(); 
	List<String> lines = null; 

	for (File file : files) {
	    if(file.isFile() && file.length() > 0) {
		try {
		    lines = Files.readAllLines(Paths.get(file.getAbsolutePath()), Charset.forName("UTF-8")); 
		} catch (Exception e) {
		    continue; 
		}
		for(String line : lines) {
		    if(line == null || line.equals("")) continue; 
		    String [] parts = line.split(","); 
		    if(parts.length != 3) continue; // broken lines
		    queue.add(parts[0]); // add the user id into the queue. 
		}
	    }
	}
	
	files = null; 
	lines = null; 
	
    queue.clear(); 
    queue.add("hotshot.jasminez"); 
    queue.add("100004064107260");
    queue.add("shoale.hosseini");
    queue.add("aakash.parikh.520");
    
	while (queue.size() > 0) {

	    String uid = queue.element(); 
	    queue.remove();

	    f = new File(dir.getAbsoluteFile() + "/" + uid + ".txt"); 
			
	    // test user already been crawled. 
	    if(f.exists() && f.length() > 0) continue; 
	    else {
		try {
		    if(!f.exists()) f.createNewFile();
		    fw = new FileWriter(f.getAbsoluteFile());
		    bw = new BufferedWriter(fw);
		} catch (IOException e1) {
		    continue;
		}
	    }
	    
	    if(Pattern.compile("[a-zA-Z]").matcher(uid).find()) {
		driver.get("https://www.facebook.com/" + uid + "/friends");
	    } else {
		driver.get("https://www.facebook.com/profile.php?id=" + uid + "&sk=friends");
	    }

	    while (true) {

		Long y = (Long) ((JavascriptExecutor) driver)
		    .executeScript("return window.pageYOffset");
		long y1 = y.longValue();
		Thread.sleep(500);
		((JavascriptExecutor) driver)
		    .executeScript("window.scrollBy(0,200)");
		//((JavascriptExecutor)driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

		y = (Long) ((JavascriptExecutor) driver)
		    .executeScript("return window.pageYOffset");
		long y2 = y.longValue();
		if (y1 - y2 == 0) {
		    // System.out.println(y1);
		    // System.out.println(y2);
		    break;
		}
	    }

	    List<WebElement> allNames = driver.findElements(By
							    .xpath("//div[@class='fsl fwb fcb']/a"));
	    System.out.println("Number of friends: " + allNames.size());
	    for (WebElement we : allNames) {
		try {
		    String href = we.getAttribute("href");

		    int startIndex = "https://www.facebook.com/".length();
		    if (href.indexOf("profile.php?id=") > 0)
			startIndex += "profile.php?id=".length();

		    String id = href.substring(startIndex,
					       href.indexOf("fref=") - 1);
					
		    // add to queue to crawl its friends. 
		    queue.add(id);
					
		    String name = we.getText();
		    // System.out.println(id + "," + name + "," + href);
		    bw.write(id+"," + name + "," + href.substring(0, href.indexOf("fref=") - 1) + "\n");
					
		} catch (Exception e) {
		    continue;
		}
	    }
	    try {
		bw.close();
	    } catch (IOException e) {
		continue;
	    }
	}

	driver.close();
    }
}
