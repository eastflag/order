package com.spider.order;

import com.spider.order.service.ParserBMService;
import com.spider.order.util.CommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class OrderApplicationTests {
    @Autowired
    private ParserBMService parserBMService;

    @Test
    void test1() {
        // 신배민 - 배달 : 후불카드
        // String hexadecimal = "1B401B2118202020202020202020202020202020B9E8B4DE20C1D6B9AEC0FCC7A520202020202020202020202020200A0D1B21001B2118C1D6B9AEB9F8C8A3204F59335420202020202020202020202020202020202020202020202020202020200A0D1B21001B2118B0E1C1A6B9E6BDC420C8C4BAD2C4ABB5E5202020202020202020202020202020202020202020202020200A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB9E8B4DEC1D6BCD23A0A0D1B2118BACEBBEA20BACEBBEAC1F8B1B820B0A1BEDFB5BF203436342D31303020BEF6B1A4B7CE20313832B9F8B1E6202032352D330A0D1B2100BACEBBEA20BACEBBEAC1F8B1B820BEF6B1A4B7CE313832B9F8B1E62032352D3320BEF6B1A4B7CE20313832B9F8B1E6202032352D330A0D0A0DBFACB6F4C3B33A0A0D1B21183031302D333433332D303330380A0D1B2100B0EDB0B4C1A4BAB8B8A620B9E8B4DEB8F1C0FB20BFDC20BBE7BFEBC7CFB0C5B3AA20BAB8B0FC2C20B0F8B0B3C7D220B0E6BFEC20B9FDC0FBC3B3B9FAC0BB20B9DEC0BB20BCF620C0D6BDC0B4CFB4D92E0A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DBFE4C3BBBBE7C7D73A2020202020202020202020202020202020202020202020202020202020202020200A0D1B2118B0A1B0D4203A20B4D9C0DAB3E0B8BE20C4DDB6F3312E3235B8AEC5CDC1D6BCBCBFE42EC8A4BDC320BCD2B1DD20B8BBB0ED20B1E2BABB20BEE7B3E4B5B520C1D6BDC3B3AABFE42028BCF6C0FAC6F7C5A92058290A0D1B21001B2118B9E8B4DE203A20BFACB6F4C1D6BCBCBFE40A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB8DEB4BAB8ED202020202020202020202020202020202020202020BCF6B7AE20202020202020B1DDBED70A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118BEF6C3BB20C5AB20C8C4B6F3C0CCB5E520C4A1C5B220202020202031202020202020202031372C3030300A0D1B21001B2118202B20BBC00A0D1B21001B2118B3EBB6FB20BAC0C5F520B0A8C0DA2020202020202020202020202031202020202020202020382C3030300A0D1B21001B2118B9E8B4DEC6C120202020202020202020202020202020202020202020202020202020202020322C3030300A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118C7D5B0E828C8C4BAD2C4ABB5E5292020202020202020202020202020202020202020202032372C3030300A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DC1D6B9AEB9F8C8A33A4231314430314F5933540A0D323032312E30392E30372031383A353020202020202020202020202020202020202020200A0D200A0D200A0D200A0D200A0D1B69";
        // 신배민 - 포장 : 결제완료
        // String hexadecimal = "1B401B2118202020202020202020202020202020C6F7C0E520C1D6B9AEC0FCC7A520202020202020202020202020200A0D1B21001B2118C1A2BCF6B9F8C8A3203130332020202020202020202020202020202020202020202020202020202020200A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DBFACB6F4C3B33A0A0D1B21183035302D33373337312D383534330A0D1B2100BEC8BDC9B9F8C8A3B4C220C1D6B9AEC1A2BCF620C8C420C3D6B4EB2032BDC3B0A320B5BFBEC820C0AFC8BFC7D5B4CFB4D92E0A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DBFE4C3BBBBE7C7D73A2020202020202020202020202020202020202020202020202020202020202020200A0D1B211828BCF6C0FAC6F7C5A92058290A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB8DEB4BAB8ED202020202020202020202020202020202020202020BCF6B7AE20202020202020B1DDBED70A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118BCD2BAD2B0EDB1E220BAA5B6C7202020202020202020202020202031202020202020202031302C3030300A0D1B21001B2118DFE6BBE7C4C920BAA5B6C72020202020202020202020202020202031202020202020202031342C3030300A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118C7D5B0E828B0E1C1A6BFCFB7E1292020202020202020202020202020202020202020202032342C3030300A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DC1D6B9AEB9F8C8A33A4231314A3030374B48500A0D323032312E30392E31332031303A343920202020202020202020202020202020202020200A0D200A0D200A0D200A0D200A0D1B69";
        // 구배민 - 배달: 후불카드
        // String hexadecimal = "1B21001B2118B6ECBED320313037B5BF20383033C8A30A0D1B21001B211828B5B5B7CEB8ED2920B0E6B1E220BFA9C1D6BDC320BCBCC1BEB7CE203230342D32320A0D1B21001B211820BFB9C0CFBCBCB6ECBED320313037B5BF20383033C8A30A0D1B21001B2118BFACB6F4C3B3203A203031302D373533362D313533370A0D1B21001D2400001D7630302C0004000A0DB9E8B4DEC1D6BCD2203A20B0E6B1E220BFA9C1D6BDC320B1B3B5BF2031323720BFB9C0CF0A0DBCBCB6ECBED320313037B5BF203820C1D6B9AE20C1A4BAB8B4C220B0EDB0B4C0C720B0B3C0CE200A0DC1A4BAB80A0DBFACB6F4C3B3203A203031302D373533362D313533370A2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B4501B0A1B0D420BFE4C3BBBBE7C7D73A200A0D1B4500B8AEBAE4C2FCBFA92028BCF6C0FAC6F7C5A92058290A0D1B2118B9E8B4DE20BFE4C3BBBBE7C7D73A200A0D1B21001B2118B9AEBED5BFA120B5CEB0ED20BAA720B4ADB7AFC1D6BCBCBFE40A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB8DEB4BA202020202020202020202020202020202020202020202020BCF6B7AE202020202020B1DDBED70A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B45013F3FC4ABC3F720202020202020202020202020202020202020202020202031202020202020392C3430300A0D1B4500202BBBE7C0CCB4D928C4B52920C3DFB0A120312C353030BFF80A0D202BBCF6C1A6B8C5C4DEBCD2BDBAC3DFB0A120312C303030BFF80A0D1B4501B9E4C3DFB0A12020202020202020202020202020202020202020202020203120202020202020203530300A0D1B45001B4501B9E8B4DEC6C120202020202020202020202020202020202020202020202020202020202020332C3030300A0D1B45002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B4501C7D5B0E8203A20202020202020202020202020202020202020202020202020202031322C39303020BFF80A0D1B45002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DC1D6B9AEB0A1B0D4203A203F3FB5B7B1EEBDBA20BFA9C1D6C1DFBED3C1A10A0DC1D6B9AEC0CFBDC3203A20323032312D30392D313328BFF9292031313A31330A0DC1D6B9AEB9F8C8A3203A204231314A303039365A490A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB9E8B4DEC1D6B9AEC0B8B7CE20C0CEC7D120BAD2C6EDC0CCB3AA20B0B3C0CEC1A4BAB820B0FCB7C30A0DC7C7C7D820B5EEC0BB20B0DEC0B8BCCCC0BB20B0E6BFEC0A0DB0EDB0B4BEC8BDC9BCBEC5CD28313630302D3938383029B7CE20BFACB6F4C1D6BCBCBFE42E0A0D200A0D200A0D200A0D1B69";
        // 구배민 - 포장: 결제 완료
        // String hexadecimal = "1B401D427920BBE7C0FCB0E1C1A620BFA9BACE3A204F201D42202020202020202020202020202020205BB0EDB0B4BFEB5D0A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1D2400001D7630302C0004000A0D1B61B9E8B4DEC1D6BCD2203A20B0E6BBF3B3B2B5B520BEE7BBEABDC320B9B0B1DDC0BE20B9B0B1DD0A0DB8AE203837322D3620313020C1D6B9AE20C1A4BAB8B4C220B0EDB0B4C0C720B0B3C0CE20C1A4BAB80A0DBFACB6F4C3B3203A2028BEC8BDC9B9F8C8A3293035302D33363239342D373531390A0D2ABEC8BDC9B9F8C8A3B4C220C1D6B9AEC1A2BCF620C8C420C3D6B4EB2033BDC3B0A320C0AFC8BF2A0A2D2D2D2D2D2D2D2D2D2D2D2D2D001B450120202020202020202020202020C6F7C0E520C1A2BCF6B9F8C8A3203131352020202020202020202020200A0D1B45001D2400001D7630302C0004000A0DB9E8B4DEC1D6BCD2203A20B0E6BBF3B3B2B5B520BEE7BBEABDC320B9B0B1DDC0BE20B9B0B1DDB8AE0A0D203837322D3620313020C1D6B9AE20C1A4BAB8B4C220B0EDB0B4C0C720B0B3C0CE20C1A4BAB80A0DBFACB6F4C3B3203A2028BEC8BDC9B9F8C8A3293035302D33363239342D373531390A0D2ABEC8BDC9B9F8C8A3B4C220C1D6B9AEC1A2BCF620C8C420C3D6B4EB2033BDC3B0A320C0AFC8BF2A0A2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B4501B0A1B0D420BFE4C3BBBBE7C7D73A200A0D1B450028BCF6C0FAC6F7C5A9204F290A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB8DEB4BA202020202020202020202020202020202020202020202020BCF6B7AE202020202020B1DDBED70A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B4501BECBC5EBC6D0C6BCC7D4B9DA20202020202020202020202020202020202031202020202020382C3930300A0D1B4500202BB0E8B6F5C8C4B6F3C0CC28B9DDBCF72920C3DFB0A120310A0D20202C303030BFF80A0D202BBDBAC7C1200A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B4501C7D5B0E8203A20202020202020202020202020202020202020202020202020202020382C39303020BFF80A0D1B45002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DC1D6B9AEB0A1B0D4203A20BECBC5EBC6D0C6BC20B9B0B1DDC1A10A0DC1D6B9AEC0CFBDC3203A20323032312D30392D313328BFF9292031333A34330A0DC1D6B9AEB9F8C8A3203A204231314A30304E4A4C4B0A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB9E8B4DEC1D6B9AEC0B8B7CE20C0CEC7D120BAD2C6EDC0CCB3AA20B0B3C0CEC1A4BAB820B0FCB7C30A0DC7C7C7D820B5EEC0BB20B0DEC0B8BCCCC0BB20B0E6BFEC0A0DB0EDB0B4BEC8BDC9BCBEC5CD28313630302D3938383029B7CE20BFACB6F4C1D6BCBCBFE42E0A0D2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A0A0D200A0D200A0D200A0D1B69";
        // 신배민 - 배민원
        // String hexadecimal = "1B4020A6A32D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2DA6A4200A0D1B21182020202020202020202020202020B9E8B9CE3120C1D6B9AEC0FCC7A520202020202020202020202020200A0D1B210020A6A62D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2DA6A5200A0D1B2118C1D6B9AEB9F8C8A3203046375620202020202020202020202020202020202020202020202020202020200A0D1B21001B2118B0E1C1A6B9E6BDC420B0E1C1A6BFCFB7E1202020202020202020202020202020202020202020202020200A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB9E8B4DEC1D6BCD23A0A0D1B2118C7D1B0ADB7CEB5BF0A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DBFE4C3BBBBE7C7D73A2020202020202020202020202020202020202020202020202020202020202020200A0D1B2118B0A1B0D4203A20B8C0C0D6B0D420B8B8B5E9BEEEC1D6BCBCBFE42028BCF6C0FAC6F7C5A92058290A0D1B21001B2118B9E8B4DE203A20B9AEBED5BFA120B5CEB0ED20BAA720B4ADB7AFC1D6BCBCBFE40A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB8DEB4BAB8ED202020202020202020202020202020202020202020BCF6B7AE20202020202020B1DDBED70A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118BEB2B8AEB6FB29B1E8C4A1C2F2202020202020202020202020202031202020202020202031342C3030300A0D1B21001B2118202B20327E33C0CE20BFEBB1E220C6F7C0E52832C0CEBAD020C0CCBBF320C1D6B9AEBDC3290A0D1B21001B2118202B20B0E8B6F5B8BBC0CC2028BCD22920C3DFB0A128352C303030BFF8290A0D1B21001B2118BEB2B8AEB6FB29B1E8C4A1C2F2202020202020202020202020202031202020202020202020392C3030300A0D1B21001B2118202B20327E33C0CE20BFEBB1E220C6F7C0E52832C0CEBAD020C0CCBBF320C1D6B9AEBDC3290A0D1B21001B2118B0F8B1E2B9E420202020202020202020202020202020202020202033202020202020202020332C3030300A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118C7D5B0E828B0E1C1A6BFCFB7E1292020202020202020202020202020202020202020202032362C3030300A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D2020202020B1E2BABB20B9E8B4DEC6C128322C35303029202B20B0C5B8AE20C7D2C1F5283029202020200A0D20202020202020202020202020C3D120B9E8B4DEC6C120322C353030BFF82020202020202020202020200A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DC1D6B9AEB9F8C8A33A5431314A30303030304637560A0D323032312E30392E31332031303A343920202020202020202020202020202020202020200A0D200A0D200A0D200A0D200A0D1B69";
        // 구배민 - 배민원
        // String hexadecimal = "1B401D427920BBE7C0FCB0E1C1A620BFA9BACE3A204F201D42202020202020202020202020202020205BB0EDB0B4BFEB5D0A0D20A6A32D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2DA6A4200A0D1B211820202020202020202020202020B9E8B9CE31C1D6B9AE20302058205120542020202020202020202020200A0D1B210020A6A62D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2DA6A5200A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1D2400001D7630302C0003000A0D1B61B9E8B4DEC1D6BCD2203A20C0BBC1F6B7CEB5BF20C1D6B9AE20C1A4BAB8B4C220B0EDB0B4C0C70A0D20B0B3C0CE20C1A4BAB80A0D2ABEC8BDC9B9F8C8A3B4C220C1D6B9AEC1A2BCF620C8C420C3D6B4EB2033BDC3B0A320C0AFC8BF2A0A2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D001B4501B9E8B4DEC1D6BCD2203A0A0D1B45001B2118C0BBC1F6B7CEB5BF0A0D1B21001D2400001D7630302C0003000A0DB9E8B4DEC1D6BCD2203A20C0BBC1F6B7CEB5BF20C1D6B9AE20C1A4BAB8B4C220B0EDB0B4C0C7200A0DB0B3C0CE20C1A4BAB80A0D2ABEC8BDC9B9F8C8A3B4C220C1D6B9AEC1A2BCF620C8C420C3D6B4EB2033BDC3B0A320C0AFC8BF2A0A2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B4501B0A1B0D420BFE4C3BBBBE7C7D73A200A0D1B4500B4A9B6F4BEC8B5C7B0D420C0DFC3ACB0DCBCAD20BAB8B3BBC1D6BCBCBFE42028BCF6C0FAC6F7C5A90A0D204F290A0D1B2118B9E8B4DE20BFE4C3BBBBE7C7D73A200A0D1B21001B2118B9AEBED5BFA1B4D9B0A120B3F6B5CEB0ED20B9AEC0DA20C1D6B0ED20B0A1BCBCBFE40A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB8DEB4BA202020202020202020202020202020202020202020202020BCF6B7AE202020202020B1DDBED70A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B4501BFD5BBF5BFECC6A2B1E8BFECB5BF2020202020202020202020202020202032202020202031342C3030300A0D1B45001B4501C3B6C6C7BBF5BFECC7D4B9DA20202020202020202020202020202020202031202020202020382C3030300A0D1B45001B4501C3BCB4D9C4A1C1EEB1E8B9E420202020202020202020202020202020202031202020202020342C3030300A0D1B45001B4501C2F7B5B9B5C8C0E5C2EEB0B320202020202020202020202020202020202031202020202020362C3530300A0D1B45002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B4501C7D5B0E8203A20202020202020202020202020202020202020202020202020202033322C35303020BFF80A0D1B45002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D2020202020B1E2BABB20B9E8B4DEC6C128332C30303029202B20B0C5B8AE20C7D2C1F5283029202020200A0D20202020202020202020202020C3D120B9E8B4DEC6C120332C303030BFF82020202020202020202020200A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DC1D6B9AEB0A1B0D4203A20B1E8B0A1B3D720B4EBC7D0B7CEBABBC1A10A0DC1D6B9AEC0CFBDC3203A20323032312D30392D313328BFF9292031313A33340A0DC1D6B9AEB9F8C8A3203A205431314A30303030305851540A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DBDD228B1B9B3BBBBEA292C20B0A5BAF1B8B8B5CE2FB6B1B8B8B5CEB1B92FB5B7B1EEBDBAB7D1B1E80A0DB9E42FB5B7B8C0B5B5BDC3B6F42FB5EEBDC9B5B7B1EEBDBA2FC4A1C1EEB5B7B1EEBDBA2FB0EDB1B80A0DB8B6B5B7B1EEBDBA2FB6F3BABAC0CC2FBABAC0BDBFECB5BF2FBDD2B6B1BABAC0CC2F31C0CEBACE0A0DB4EBC2EEB0B32FC3B6C6C7BBF5BFECC7D4B9DA2FC4A1C1EEB6B1BABAC0CC2FC4BFB8AEB5A4B9E4280A0DB5B7B1EEBDBA2920B5C5C1F6B0EDB1E228B1B9B3BBBBEA292C20B1E8B0A1B3D7B1E8B9E42F0A0DC3BCB4D9C4A1C1EEB1E8B9E42FC2FCC4A128BFF8BEE7BBEA29B8B6BFE4B1E8B9E42FBFC0B9C9B7BF0A0DB6F3C0CCBDBA2FBAFCBFA1BEDFBBF5BFECBABAC0BDB9E420B5C5C1F6B0EDB1E228B1B9B3BBBBEA0A0D292C20BDBAC6D4B1E8B9E42FBDBAC6D4B1E8C4A1BABAC0BDB9E42FBDBAC6D4BFBEB3AFB5B5BDC3B6F40A0D2FC3B6C6C7C4A1C1EEB1E8C4A1BABAC0BDB9E42F31C0CEBACEB4EBC2EEB0B320B5C5C1F6B0EDB1E20A0D28BFDCB1B9BBEA3AB9CCB1B92CBDBAC6E4C0CEB5EE202F20B1B9B3BBBBEAC8A5C7D5292C20B1E80A0DC4A1C2EEB0B320B5C5C1F6B0EDB1E228B9CCB1B9BBEA292C2031C0CEBAD0BACEB4EBC2EEB0B30A0D20B5C5C1F6B0EDB1E228B9CCB1B9BBEA292C20B8C5C4DEC1A6C0B0B1E8B9E42FBFC0BAD2B5A4B9E40A0D20B5C5C1F6B0EDB1E228BFDCB1B9BBEA3AC4A5B7B92CC6E4B7E7202CC1DFB1B9292C20B6B10A0DB8B8B5CEB1B92FB5B9BCDCBAF1BAF6B9E42FB9F6BCB8BAD2B0EDB1E2B1E8B9E42FBCD2B0EDB1E2C0B00A0DB0B3C0E52FBCD2B0EDB1E2C1D6B8D4B9E42FBCD2BAD2B0EDB1E2B5A4B9E42FBFC0B9C9B7BFB6F3C0CC0A0DBDBA2F31C0CEBACEB4EBC2EEB0B32FC4BFB8AEB5A4B9E43ABCD2BAD2B0EDB1E22CB5B7B1EEBDBA0A0D20BCD2B0EDB1E228C8A3C1D6BBEA292C20C3B6C6C7BBF5BFECC7D4B9DA20BCD2B0EDB1E228B1B90A0DB3BBBBEA2CC8A3C1D6BBEA20C8A5C7D5292C20C2F7B5B9B5C8C0E5C2EEB0B320BCD2B0EDB1E2280A0DB9CCB1B9BBEA292C2031C0CEBACEB4EBC2EEB0B320B4DFB0EDB1E228B9CCB1B9BBEA292C200A0DB0A5BAF1B8B8B5CE2FC2F7B4DFB9B0C2CCB8E92FC4A1C5B2BABAC0BDB9E42FC4A1C5B2BFA1B1D7B1E80A0DB9E420B4DFB0EDB1E228B1B9B3BBBBEA292C20BCF8B5CEBACEC2EEB0B320C4E128B9CCB1B9BBEA0A0D292C20C4E1B1B9BCF620C4E128B1B9B3BBBBEA292E20BFC0C2A1BEEEC2EAC0AFBABAC0BDB9E420BFC00A0DC2A1BEEE28BFF8BEE7BBEA292C20BABAC0BDBFECB5BF2FBFC0BAD2B5A4B9E42FC7D8B9B0C2ABBBCD0A0DB6F3B8E92FC7D8BDC5BABAC0BDB9E420BFC0C2A1BEEE28BFDCB1B9BBEA3AC4A5B7B92CC6E4B7E70A0D2CC1DFB1B9292C20B3ABC1F6B5A4B9E420B3ABC1F628C1DFB1B9BBEA292C20B1E8C4A1C2EEB0B30A0D2FB5B7B8C0B5B5BDC3B6F42FB5B9BCDCBAF1BAF6B9E42FBDBAC6D4BFBEB3AFB5B5BDC3B6F42FBDBA0A0DC6D4B1E8C4A1BABAC0BDB9E42FC3B6C6C7C4A1C1EEB1E8C4A1BABAC0BDB9E420B9E8C3DFB1E8C4A10A0DB9E8C3DF28B1B9B3BBBBEA292C20B1E8C4A1C2EEB0B320B9E8C3DFB1E8C4A120B0EDC3E5B0A1B7E70A0D28B1B9B3BBBBEA292CB1E8C4A12FBAF1BAF6B1B9BCF62FB1E8C4A1B8BBC0CCB1B9BCF62F31C0CE0A0DBACEB4EBC2EEB0B320B9E8C3DFB1E8C4A120B9E8C3DF28B1B9B3BBBBEA292CB1E8C4A12FBAF10A0DBAF6B1B9BCF62FB1E8C4A1B8BBC0CCB1B9BCF62F31C0CEBACEB4EBC2EEB0B320B9E8C3DFB1E8C4A10A0D20B0EDC3E5B0A1B7E728B1B9B3BBBBEA2CC1DFB1B9BBEA20C8A5C7D5292FC2FCC4A128BFF80A0DBEE7BBEA290A0D200A0D200A0D200A0D1B69";
        // 배민라이더스 - 배달
        // String hexadecimal = "1B401B2118202020202020202020202020202020B9E8B4DE20C1D6B9AEC0FCC7A520202020202020202020202020200A0D1B21001B2118C1D6B9AEB9F8C8A3203443483620202020202020202020202020202020202020202020202020202020200A0D1B21001B2118B0E1C1A6B9E6BDC420B0E1C1A6BFCFB7E1202020202020202020202020202020202020202020202020200A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB9E8B4DEC1D6BCD23A0A0D1B2118B4EBC8EFB5BF0A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DBFE4C3BBBBE7C7D73A2020202020202020202020202020202020202020202020202020202020202020200A0D1B2118B0A1B0D4203A20C4A1C5B2B9AB20BEC8C1D6BCC5B5B520B1A6C2FABEC6BFE4210A0D1B21001B2118B9E8B4DE203A20C1B6BDC9C8F720BEC8C0FCC7CFB0D420BFCDC1D6BCBCBFE4203A290A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB8DEB4BAB8ED202020202020202020202020202020202020202020BCF6B7AE20202020202020B1DDBED70A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118B9D9C0CCC3F7C7CFC7C1C6D120202020202020202020202020202031202020202020202031322C3430300A0D1B21001B2118202B20C4C9C0CCC1D8C6F7C5D7C0CCC5E40A0D1B21001B2118202B20C6E9BDC3C4DDB6F30A0D1B21001B2118202B20BCF8BBECB4CFC7CFBFC028312C303030BFF8290A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118C7D5B0E828B0E1C1A6BFCFB7E1292020202020202020202020202020202020202020202031322C3430300A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DC1D6B9AEB9F8C8A33A5430575830303030344348360A0D323032312E30332E33312032323A303120202020202020202020202020202020202020200A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DBCD2B0EDB1E228C8A3C1D6BBEA3AC6D0C6BC2920B5C5C1F6B0EDB1E228B1B9BBEA3AC6D0C6BC2920B4DFB0EDB1E228BAEAB6F3C1FABBEA3AC4A1C5B2B9F6B0C5290A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D200A0D200A0D200A0D200A0D1B69";
        // 배민라이더스 - 포장
        //String hexadecimal = "1B401B2118202020202020202020202020202020C6F7C0E520C1D6B9AEC0FCC7A520202020202020202020202020200A0D1B21001B2118C1A2BCF6B9F8C8A3203136392020202020202020202020202020202020202020202020202020202020200A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DBFACB6F4C3B33A0A0D1B21183035302D33373335312D323138300A0D1B2100BEC8BDC9B9F8C8A3B4C220C1D6B9AEC1A2BCF620C8C420C3D6B4EB2032BDC3B0A320B5BFBEC820C0AFC8BFC7D5B4CFB4D92E0A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DB8DEB4BAB8ED202020202020202020202020202020202020202020BCF6B7AE20202020202020B1DDBED70A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118BBF5BFECB9F6B0C520BCBCC6AE202020202020202020202020202032202020202020202031312C3630300A0D1B21001B2118202B20C6F7C5D7C0CCC5E40A0D1B21001B2118202B20C6E9BDC3C4DDB6F30A0D1B21001B2118C4C9C0CCC1D8C4A1C5B2B9F6B0C52020202020202020202020202031202020202020202020342C3030300A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D1B2118C7D5B0E828B0E1C1A6BFCFB7E1292020202020202020202020202020202020202020202031352C3630300A0D1B21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DC1D6B9AEB9F8C8A33A5430575830303030334B4D530A0D323032312E30332E33312031393A323320202020202020202020202020202020202020200A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0DBCD2B0EDB1E228C8A3C1D6BBEA3AC6D0C6BC2920B5C5C1F6B0EDB1E228B1B9BBEA3AC6D0C6BC2920B4DFB0EDB1E228BAEAB6F3C1FABBEA3AC4A1C5B2B9F6B0C5290A0D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0A0D200A0D200A0D200A0D200A0D1B69";
        // 요기요 - 배달(후불카드)
        // String hexadecimal = "5B00B0ED00B0B400BFEB005D0D0A1B321B61001B45001B21001B4D001D42001B21301B45011B6101BFE400B1E200BFE40D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B45011D2101C1D600B9AE20B9F800C8A3003A202300330031003600340D0A1B321B61001B45001B21001B4D001D42001D21001B45011D2101B0E100C1A620B9E600B9FD003A20C7F600C0E500C4AB00B5E500B0E100C1A60D0A1B321B61001B45001B21001B4D001D42001D21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0ABFE400C3BB20BBE700C7D7003A0D0A1B321B61001B45001B21001B4D001D42001B45011D2101C0CF00C8B800BFEB20BCF600C0FA002C20C6F700C5A900B4C220BEC820BEB500B0D400BFE4002E0D0A1B321B61001B45001B21001B4D001D42001D21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0AB9E800B4DE20C1D600BCD2003A0D0A1B321B61001B45001B21001B4D001D42001B45011D21012800B5B500B7CE00B8ED002920C3E600C3BB00BACF00B5B520C3BB00C1D600BDC320C8EF00B4F600B1B820C7B300BBEA00B7CE0031003200380D0AB9F800B1E620320036002D0033203100C3FE00310030003400C8A30D0A1B321B61001B45001B21001B4D001D42001D21002800C1F600B9F8002920C3E600C3BB00BACF00B5B520C3BB00C1D600BDC320C8EF00B4F600B1B820BAB900B4EB00B5BF202800BAB900B4EB0D0A3200B5BF00292032003000370033203100C3FE00310030003400C8A30D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B4501BFAC00B6F400C3B3003A2030003500300034003400370035003200370037003100350D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B4501B8DE00B4BA00B8ED20202020202020202020202020202020202020202020BCF600B7AE202020202020B0A100B0DD0D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B45011D2101B1E200BABB20B8DE00B4BA003A20B6B100BABA00C0CC202020202020202020202020203120202020202033002C0035003000300D0A1B321B61001B45001B21001B4D001D42001D21002D20B8C020BCB100C5C3003A20BCF800C7D100B8C0202020202020202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42001B45011D2101B1E200BABB20B8DE00B4BA003A20BDC500C0FC00C2FB00BCF800B4EB002800B3BB00C0E500BEF820203120202020202033002C0035003000300D0AC0BD00290028004E004500570029202020202020202020202020202020202020202020202020202020202020202020200D0A1B321B61001B45001B21001B4D001D42001D21001B45011D2101B1E200BABB20B8DE00B4BA003A20B9CC00B4CF00C7D600B5B500B1D7203200B0B320202020203120202020202031002C0035003000300D0A1B321B61001B45001B21001B4D001D42001D2100B9E800B4DE00B7E1003A20202020202020202020202020202020202020202020202020202020202033002C0030003000300D0A1B321B61001B45001B21001B4D001D4200310032002C00300030003020C3D600BCD200C1D600B9AE00B1DD00BED7003A202020202020202020202020202020202033002C0035003000300D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B45011D2101C7D500B0E8003A20202020202020202020202020202020202020202020202020202020202020310035002C0030003000300D0A1B321B61001B45001B21001B4D001D42001D21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0AC1D600B9AE20B8C500C0E5003A20BDC500C0FC00B6B100BABA00C0CC002D00C3BB00C1D600B0AD00BCAD00C1A10D0A1B321B61001B45001B21001B4D001D4200C1D600B9AE20B9F800C8A3003A20460032003200300038003200340031003600330038004A00440054004400300D0A1B321B61001B45001B21001B4D001D4200C1D600B9AE20C0CF00C0DA003A203200300032003200B3E22030003800BFF92032003400C0CF002800BCF6002920BFC000C8C400300034003A003300380D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0ABFF800BBEA00C1F6003A20BDD2002800B1B900B3BB00BBEA002920B6B1002800B9D00029002800B9CC00B1B900BBEA002C20C8A300C1D600BBEA002C200D0AC5C200B1B900BBEA002920C7DC00B5C500C1F600B0ED00B1E2002800BCF600C0D400BBEA002C20B1B900B3BB00BBEA002920B8B800B5CE00B5C50D0AC1F600B0ED00B1E2002800B1B900B3BB00BBEA002920C2FC00C4A100B0A100B4D900B6FB00BEEE002800BFF800BEE700BBEA002C20C5C200C6F200BEE70D0A2920B1E800C4A100B9E800C3DF002800C1DF00B1B900BBEA002920C4A100C5B200B8B500B4DF002800B1B900B3BB00BBEA002920BFC000C2A100BEEE0D0AC6A200B1E800BFC000C2A100BEEE002800C4A500B7B900BBEA002920B9CC00B4CF00C7D600B5B500B1D7002800B5C500C1F600B0ED00B1E2002C20B4DF0D0AB0ED00B1E220BCAF00C0BD002C20B1B900B3BB00BBEA00290D0A1B321B61001B45001B21001B4D001D42000D0A0D0A0D0A0D0A0D0A0D0A0D0A1D5601";
        // 요기요 - 포장
        // String hexadecimal = "5B00B0ED00B0B400BFEB005D0D0A1B321B61001B45001B21001B4D001D42001B21301B45011B6101BFE400B1E200BFE420C6F700C0E50D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B45011D2101B1B300C8AF20C4DA00B5E5003A20370033005A004E003000340032003100520D0A1B321B61001B45001B21001B4D001D42001D21001B45011D2101B0E100C1A620B9E600B9FD003A20BFE400B1E200BFE400B0E100C1A600BFCF00B7E10D0A1B321B61001B45001B21001B4D001D42001D21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0ABFE400C3BB20BBE700C7D7003A0D0A1B321B61001B45001B21001B4D001D42001B45011D2101C0CF00C8B800BFEB20BCF600C0FA002C20C6F700C5A900B4C220BEC820BEB500B0D400BFE4002E0D0A1B321B61001B45001B21001B4D001D42001D21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B4501BFAC00B6F400C3B3003A203000310030003900320031003800330032003600340D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B4501B8DE00B4BA00B8ED20202020202020202020202020202020202020202020BCF600B7AE202020202020B0A100B0DD0D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B45011D2101BCC200C6AE20B8DE00B4BA00280053004100560049004E004700532053004500540029003A205B00C1D62020203120202020202038002C0039003000300D0AB9AE00BCF6003100C0A7005D00C7D100C0D400B0AD00C1A4205300450054202020202020202020202020202020202020202020200D0A1B321B61001B45001B21001B4D001D42001D21002D20B0AD00C1A4003A20C4C900C0CC00C1D800B0AD00C1A4002800BCD2002920202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20B9F600B0C520BAAF00B0E620BCB100C5C3003A20B9F600B0C500BFEE00C4A100C5B220203120202020202020202020200D0AB9F600B0C5002800B1E200BABB002920202020202020202020202020202020202020202020202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C0BD00B7E100BCB100C5C3003A20C4DD00B6F3002800B1E200BABB002920202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B45011D2101C7D500B0E8003A202020202020202020202020202020202020202020202020202020202020202038002C0039003000300D0A1B321B61001B45001B21001B4D001D42001D21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0AC1D600B9AE20B8C500C0E5003A20B9F600B0C500BFEE00B9F600B0C5002D00C3B500BEC800B5CE00C1A400C1A10D0A1B321B61001B45001B21001B4D001D4200C1D600B9AE20B9F800C8A3003A20460032003200300038003200330031003600350030004A00450046004100340D0A1B321B61001B45001B21001B4D001D4200C1D600B9AE20C0CF00C0DA003A203200300032003200B3E22030003800BFF92032003300C0CF002800C8AD002920BFC000C8C400300034003A003500310D0A1B321B61001B45001B21001B4D001D4200B9E600B9AE20BDC300B0A3003A203200300032003200B3E22030003800BFF92032003300C0CF002800C8AD002920BFC000C8C400300035003A003100300D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A0D0A0D0A0D0A0D0A0D0A0D0A0D0A1D5601";
        // 요기요익스프레스 - 배달
        // String hexadecimal = "5B00B0ED00B0B400BFEB005D0D0A1B321B61001B45001B21001B4D001D42001B21301B45011B6101BFE400B1E200BFE420C0CD00BDBA00C7C100B7B900BDBA0D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B45011D2101C1D600B9AE20B9F800C8A3003A202300320035003100310D0A1B321B61001B45001B21001B4D001D42001D21001B45011D2101B0E100C1A620B9E600B9FD003A20BFE400B1E200BFE400B0E100C1A600BFCF00B7E10D0A1B321B61001B45001B21001B4D001D42001D21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0ABFE400C3BB20BBE700C7D7003A0D0A1B321B61001B45001B21001B4D001D42001B45011D2101C0CF00C8B800BFEB20BCF600C0FA002C20C6F700C5A900B4C220BEC820BEB500B0D400BFE4002E205B00BEC800C0FC00B9E800B4DE005D200D0AB9AE20BED500BFA120B3F500B0ED002C20B9AE00C0DA00C1D600BCBC00BFE400210D0A1B321B61001B45001B21001B4D001D42001D21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B4501BFAC00B6F400C3B3003A2030003500300034003400380031003700330030003800330D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B4501B8DE00B4BA00B8ED20202020202020202020202020202020202020202020BCF600B7AE202020202020B0A100B0DD0D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B45011D2101B8DE00C0CE20B8DE00B4BA003A203100C0CE00BAD020BDC500B3BB00B8B020B8B600B6F32020312020202020310034002C0039003000300D0AC5C100280031003200B0A100C1F600C5E400C7CE00BCBF00C7C100C3DF00B0A1002920202020202020202020202020202020202020200D0A1B321B61001B45001B21001B4D001D42001D21002D20BBE700C0CC00C1EE20BCB100C5C3003A20B8B600B6F300C5C12034003000300047202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20B0ED00B1E220BCB100C5C3003A20BCD200B0ED00B1E2002800BFEC00BBEF00B0E300292020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20B8C020BCB100C5C3003A203100B4DC00B0E820BCF800C7D100B8C0002800BEF300BEF320203120202020202020202020200D0AC7CF00B1E200B8B8002920202020202020202020202020202020202020202020202020202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20BCF700C1D62020202020202020202020202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20C3BB00B0E600C3A4202020202020202020202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20C8E600B8F100C0CC00B9F600BCB82020202020202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20B0C700B5CE00BACE202020202020202020202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20C7AA00C1D62020202020202020202020202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20280032002900BAD000B8F000C0DA005F0035003500472020202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20C1DF00C8AD00B4E700B8E9002800BEE300C0BA00B8E900292020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20280032002900B3D000C0FB00B4E700B8E9002800BBFE00BAEA0029005F0034003020203120202020202020202020200D0A4720202020202020202020202020202020202020202020202020202020202020202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20280032002900BBF500BFEC00BFCF00C0DA005F003200340047202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20280032002900C7C700BDAC00BABC005F0032003000472020202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20280033002900BDBA00B8F000C5A900C7DC005F003400300047202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D42002D20C5E400C7CE003A20280032002900B0ED00B1B800B8B600B6B1005F003200380047202020202020203120202020202020202020200D0A1B321B61001B45001B21001B4D001D4200B9E800B4DE00B7E1003A20202020202020202020202020202020202020202020202020202020202031002C0039003000300D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A1B45011D2101C7D500B0E8003A20202020202020202020202020202020202020202020202020202020202020310036002C0038003000300D0A1B321B61001B45001B21001B4D001D42001D21002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0AC1D600B9AE20B8C500C0E5003A20B8B600B6F300C0C700BDC500B8B600B6F300C5C1002600B8B600B6F300BCA700B1C50D0A1B321B61001B45001B21001B4D001D4200C1D600B9AE20B9F800C8A3003A20460032003200300038003200330031003900330035004A00440055003300360D0A1B321B61001B45001B21001B4D001D4200C1D600B9AE20C0CF00C0DA003A203200300032003200B3E22030003800BFF92032003300C0CF002800C8AD002920BFC000C8C400300037003A003300360D0A1B321B61001B45001B21001B4D001D42002D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D2D0D0A0D0A0D0A0D0A0D0A0D0A0D0A0D0A1D5601";

        String hexadecimal = "A1D8B0EDB0B4C1A4BAB8B5B5BFEBBDC320B9FDC0FBC3A5C0D3C0BB20B9DEC0BBBCF6C0D6BDC0B4CFB4D9";

//        String orderAppKind = parserService.checkOrderAppKind(hexadecimal);
        List<String> encodedList = Arrays.asList(hexadecimal.split("0A"));

        for (String result : encodedList) {
            System.out.println("encoded:" + result);
            System.out.println("decoded:" + CommonUtil.decodeSH(result));
        }

//        ServerRequestDTO serverRequestDTO = parserService.parse(resultList, orderAppKind);
//        System.out.println(serverRequestDTO.toString());
    }

    @Test
    void test2() {
        String order = "소고기(2000)";
        int left = order.lastIndexOf("(");
        int right = order.lastIndexOf(")");
        String strPrice = order.substring(left + 1, right);
        try {
            Integer.parseInt(strPrice);
            System.out.println("success");
        } catch (Exception e) {
            System.out.println("fail: " + e.getMessage());
        }
    }

    @Test
    void test3() {
        String menu = "엄청 큰 후라이드 치킨      1        17,000";


//        System.out.println(menu.substring(0, 18).replace(" ", "x"));
//        // 메뉴 수량, 금액 파싱
//        String[] quantityAndPrice = menu.substring(18).split(" ");
//        System.out.println(quantityAndPrice[0].replace(" ", "x"));
//        System.out.println(quantityAndPrice[quantityAndPrice.length - 1].replace(" ", "x"));

        String[] menus = menu.split("  ");
        System.out.println(menus.length);
        // 메뉴 수량, 금액 파싱
        for (String s : menus) {

            System.out.println(s);
        }
//        String[] quantityAndPrice = menu.substring(18).split(" ");
//        System.out.println(quantityAndPrice[0].replace(" ", "x"));
//        System.out.println(quantityAndPrice[quantityAndPrice.length - 1].replace(" ", "x"));
    }

    @Test
    void test4() throws NumberFormatException {
        // 요기요: 2022년 08월 24일(수) 오후04:38
        String orderDate = "2022년 08월 24일(수) 오후04:38";
        String yyyyMMdd = orderDate.substring(0, 12).replace("년 ", "").replace("월 ", "");
        String amPm = orderDate.substring(17, 19);
        String hh = orderDate.substring(19, 21);
        String mm = orderDate.substring(22);

        if (amPm.indexOf("오후") >= 0) {
            int intHH = Integer.parseInt(hh) + 12;
            hh = String.valueOf(intHH);
        }

        System.out.println(yyyyMMdd + hh + mm);
    }

    @Test
    void test5() {
        String orderAppKind = "YE";
        System.out.println(orderAppKind.substring(0, 2));

    }
}
