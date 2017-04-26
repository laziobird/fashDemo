import scrapy
class DmozSpider(scrapy.Spider):
    name = "dmoz"
    allowed_domains = ["dmoz.org"]
    start_urls = [
        "https://mart.coding.net/projects/"
]
    def parse(self, response):
      for sel in response.xpath('//div[@class="detail"]'):
        name = sel.xpath('div[@class="name"]/a/text()').extract()
        price = sel.xpath('div[@class="price"]/span[@class="tabc"][1]/span/text()').extract()
        coders = sel.xpath('div[@class="coders"]/span[@class="winner"]/text()').extract()
        print(name, price, coders)