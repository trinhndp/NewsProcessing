package Controller;
import java.util.LinkedList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg {
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
	private Document htmlDocument;
	private List<String> links = new LinkedList<String>(); // Just a list of
															// URLs

	public void crawlLinks(String url) {
		try {
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT).postDataCharset("UTF-8");
			Document htmlDocument = connection.get();
			this.htmlDocument = htmlDocument;

			System.out.println("Received web page at " + url);

			Elements listNews = htmlDocument.getElementsByClass("list_news").select("li");
			do {
				Element titleNews = listNews.first().getElementsByTag("a").get(0);
				String link = titleNews.attr("abs:href");
				if (link.contains("/photo/") || link.contains("/infographics/") || link.contains("/video/"))
					return;
				else
					this.links.add(link);
				listNews = listNews.next();
			} while (!listNews.isEmpty());

			System.out.println("Size of links " + links.size());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void crawlPageContent(String pageUrl, FileWriter fileWriter) {
		try {
			Connection connection = Jsoup.connect(pageUrl).userAgent(USER_AGENT).postDataCharset("UTF-8");
			Document htmlDocument = connection.get();
			this.htmlDocument = htmlDocument;

			System.out.println("Crawl data at " + pageUrl);

//			Element blockTimer = htmlDocument.getElementsByClass("block_timer_share").first();
//			System.out.println("Timer:  " + blockTimer.text());
//			System.out.println("Title:  " + titleNews.text());
//			System.out.println("ShortIntro: " + shortIntro.text());
//			System.out.println("Content: ");
			Element titleNews = getTitleFromPageUrl(pageUrl);
			Elements detailContent = getContentFromPageUrl(pageUrl);
			fileWriter.write(titleNews.text() + "\n");
			
			if(!pageUrl.contains("/goc-nhin/"))
			{
				Element shortIntro = htmlDocument.getElementsByClass("short_intro").first();		
				fileWriter.write(shortIntro.text() + "\n");
			}

			do {
				Element detail = detailContent.remove(0);
				fileWriter.write(detail.text() + "\n");
//				System.out.println(detail.text());			
			} while (!detailContent.isEmpty());

			System.out.println();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public List<String> getLinks() {
		return this.links;
	}

	private Elements getContentFromPageUrl(String pageUrl) {
		Elements detailContent;
		detailContent = htmlDocument.getElementsByClass("fck_detail").select("p");

		if (detailContent.isEmpty())
			detailContent = htmlDocument.getElementsByClass("Normal").select("p");

		return detailContent;
	}
	
	private Element getTitleFromPageUrl(String pageUrl) {
		Element titleNews;
		if (pageUrl.contains("/goc-nhin/"))
			titleNews = htmlDocument.getElementsByClass("title_gn_detail").get(0);
		else titleNews = htmlDocument.getElementsByClass("title_news").first();
		return titleNews;
	}

	public void printOut() {
		for (String x : links) {
			System.out.println(x);
		}
	}
}
