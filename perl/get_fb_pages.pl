#!/usr/bin/perl
# download facebook info, wall and friend pages. 
# this process can be multi-threaded. 
use WWW::Mechanize; 
use HTTP::Cookies;
use CGI;
use Set::Scalar;
use get_friends qw( get_friends );
use Error qw( try except ); 

our $downloader = WWW::Mechanize->new(); 

&login(); 
# --------------------------------------------------
# Files to store the data. 
# --------------------------------------------------
open(INFODB, ">infodata.txt") || die("File open error."); 
open(WALLDB, ">walldata.txt") || die("File open error."); 
open(FNDDB, ">friendsdata.txt") || die("File open error."); 

# $doneset contains user ids that have already been crawled. 
# $todoset contains user ids that are ready to be crawled. 
my $doneset = new Set::Scalar(); 
my $todoset = new Set::Scalar("shumin.guo"); 

while (defined(my $todouser = $todoset->each)) {
    $/ = "";			# paragraph mode. 
    my $f_id_type = "NUM";	# NUM or NICK.
    my $baseurl = "http://www.facebook.com/";
    if ($todouser =~ m/^[0-9]+$/) {
	$f_id_type = "NUM";
	$baseurl .= "profile.php?id=" . $todouser; 
    } else {
	$f_id_type = "NICK";
	$baseurl .= $todouser;
    }

    try {
    print "processing user --->>> " . $todouser . "\n"; 
    # ----------------------------------------
    # download info page. 
    # ----------------------------------------
    my $infourl; 
    if ($f_id_type =~ /NUM/) {
	$infourl = $baseurl . "&sk=info";
    } else {
	$infourl = $baseurl . "?sk=info";
    }
    $downloader->get($baseurl . "?sk=info");
    $_ = $downloader->content();

    s/\n//g;			# combine lines into one. 
    s/  +/ /g; 
    s/<script[^>]*>.*?<\/script>//g;
    print INFODB $_ . "\n";

    # ----------------------------------------
    # download wall page. 
    # ----------------------------------------
    my $wallurl; 
    if ($f_id_type =~ /NUM/) {
	$wallurl = $baseurl . "&sk=wall";
    } else {
	$wallurl = $baseurl . "?sk=wall"; 
    }
    $downloader->follow_link(url =>  $wallurl); 
    $_ = $downloader->content(); 
    s/\n//g;
    s/  +/ /g; 
    s/<script[^>]*>.*?<\/script>//g;
    print WALLDB $_ . "\n";

    # ----------------------------------------
    # download friend lists. 
    # ----------------------------------------
    my $fndurl; 
    if ($f_id_type =~ /NUM/) {
	$fndurl = $baseurl . "&sk=friends";
    } else {
	$fndurl = $baseurl . "?sk=friends"; 
    }
    $downloader->follow_link(url =>  $fndurl); 
    $_ = $downloader->content(); 
    s/\n//g;
    s/  +/ /g; 
    s/<script[^>]*>.*?<\/script>//g;
    print FNDDB $_ . "\n";
    } except {
	print "Error getting document, trying to re-login. \n"; 
	&login(); 
    }

    # user crawl is done, move user from todo set to done set.
    $todoset->delete($todouser); 
    $doneset->insert($todouser);

    # build the social graph. 
    my @friends = get_friends($_); 
    for my $fnd (@friends) {
	# if user hasn't been crawled, put it to set.
	# else igore it.
	if (!$doneset->has($fnd)) {
	    $todoset->insert($fnd);
	}
    }
}

close INFODB; 
close WALLDB; 
close FNDDB; 

sub login {
    my $login = 'gsmsteve@gmail.com'; 
    my $passwd = 'Gsm1011!'; 
    my $fb_login_url = "https://login.facebook.com/login.php"; 
    $downloader->cookie_jar(HTTP::Cookies->new());

    # login to facebook. 
    $downloader->get($fb_login_url);
    $downloader->submit_form(
	form_number => 1,
	fields => { email => $login, pass => $passwd },
	);
    die unless ($downloader->success); 
    return; 
}
