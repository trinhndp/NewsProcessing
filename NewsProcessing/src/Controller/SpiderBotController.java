package Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class SpiderBotController {
	private static final int MAX_PAGES_TO_SEARCH = 20;
	private Collection<String> cateId = new Vector<String>();
	private Set<String> pagesVisited = new HashSet<String>();
	private HashSet<String> pagesToVisit = new HashSet<String>();

	public void searchLinks(String url, String fromDate, String toDate) {

		initializeCategory();

		Iterator<String> i = cateId.iterator();
		String currentUrl = createUrl(url, i.next(), fromDate, toDate);

		while (this.pagesToVisit.size() < MAX_PAGES_TO_SEARCH) {
			SpiderLeg leg = new SpiderLeg();
			leg.crawlLinks(currentUrl); // Lots of stuff happening here. Look at
										// the crawl method in SpiderLeg
			this.pagesToVisit.addAll(leg.getLinks());
			if (i.hasNext())
				currentUrl = createUrl(url, i.next(), fromDate, toDate);
		}
		System.out.println("\n**Continue to process page*");
		this.processPages("25-5-2017");
	}

	public void processPages(String date){

		Iterator<String> x = pagesToVisit.iterator();
		String currentUrl = x.next();
		int orderOfPaper = 1; // the order of paper
		File dir = new File("papers/" + date);  // initialize the path for everyday's papers
		dir.mkdir();
	
		try{
		while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
			if (!this.pagesVisited.contains(currentUrl)) {
				String fileName = "/paper" + orderOfPaper +".txt";
				File file = new File(dir, fileName);
				FileWriter fileWriter = new FileWriter(file);
				
				SpiderLeg leg = new SpiderLeg();
				leg.crawlPageContent(currentUrl, fileWriter);
				this.pagesVisited.add(currentUrl);
				if(x.hasNext()) currentUrl = x.next();
				
				fileWriter.close();
				orderOfPaper++;
			}
		}
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	public void printOut() {
		for (String x : pagesVisited) {
			System.out.println(x);
		}
	}

	private void initializeCategory() {
		cateId.add("1003450"); // Góc nhìn
		cateId.add("1001002"); // Thế giới
		cateId.add("1003497"); // Giáo dục
		cateId.add("1001005"); // Thời sự
		cateId.add("1001007"); // Pháp Luật
		cateId.add("1001009"); // Khoa học
		cateId.add("1002966"); // Gia đình
		cateId.add("1003159"); // Kinh doanh
		//cateId.add("1003750"); // Sức khoẻ
		//cateId.add("1001012"); //Cộng đồng
	}

	public String createUrl(String url, String categoryId, String fromDate, String toDate) {
		String newUrl = url + "/category/day/?cateid=" + categoryId + "&fromdate=" + fromDate + "&todate=" + toDate
				+ "&allcate=" + categoryId + "||";
		return newUrl;
	}

	public static void main(String[] args) {
		SpiderBotController sbc = new SpiderBotController();
		System.out.println("Đây là kết quả mà chúng ta thấy được \n");
		String url = "http://vnexpress.net";
		sbc.searchLinks(url, "1495645200", "1495728744"); //25-5-2017
		System.out.println("Đây là 10 trang mà chúng ta lấy \n");
		sbc.printOut();
	}
}
