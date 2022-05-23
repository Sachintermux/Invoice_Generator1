package com.professionalcipher.invoicegenerator.fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.professionalcipher.invoicegenerator.R;
import com.professionalcipher.invoicegenerator.activitys.ShowFinalPdf_Activity;
import com.professionalcipher.invoicegenerator.datamodels.CustomerList_Model;
import com.professionalcipher.invoicegenerator.datamodels.ProductList_Model;
import com.professionalcipher.invoicegenerator.savedata.SaveDataInSharePref;
import com.professionalcipher.invoicegenerator.viewmodels.MakeQuotationView_Model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Preview_Fragment extends Fragment {

    private int invoiceNo;
    private MakeQuotationView_Model view_model;
    private PDFView pdfView;
    private Button generatePdf_btn;
    private PreviewF_interface previewF_interface;
    private File file = null;
    private HashMap<String, String> companyFragmentData = new HashMap<>();
    private CustomerList_Model customerList_model;
    private SaveDataInSharePref sharePref = new SaveDataInSharePref();

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        View view = inflater.inflate(R.layout.fragment_preview_, container, false);


        initView(view);

        allClickHandle();

        return view;
    }

    private void showDialog_ErrorFinder( String title, String message, int position ) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int which ) {
                        previewF_interface.closePreview_fragment(position);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void initView( View view ) {
        previewF_interface = (PreviewF_interface) requireContext();
        pdfView = view.findViewById(R.id.pdfViewer_previewFragment);
        generatePdf_btn = view.findViewById(R.id.generatePdfBtn_previewFragment);
        view_model = new ViewModelProvider(requireActivity()).get(MakeQuotationView_Model.class);


    }

    private void observers() {
        try {
            view_model.companyFragment_DATA.observe(requireActivity(), data -> {
                companyFragmentData = data;
            });
            view_model.customerList_ModelView.observe(requireActivity(), data -> {
                customerList_model = data.get(view_model.currentCustomerSelectedPosition.getValue());
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void onPause() {
        if (file != null) {
            file.delete();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (view_model.companyFragment_DATA.getValue().size() == 0)
            showDialog_ErrorFinder("Add Company", "Please Add Your Company or Shop detail", 0);
        else if (view_model.currentCustomerSelectedPosition.getValue() == -1)
            showDialog_ErrorFinder("Add Customer", "Please Add Customer detail or Select Customer", 1);
        else if (view_model.selectedProductList.getValue().size() == 0)
            showDialog_ErrorFinder("Add Product", "Please Add Product detail or Select Product", 2);
        else {
            try {
                observers();
                showPdf();
            } catch (Exception e) {
                System.out.println("Resume  = " + e);
                Toast.makeText(requireContext(), "Resume " + e, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void allClickHandle() {

        generatePdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                try {
                    File from = new File(file.getAbsolutePath());
                    File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Invoice Generator/" + file.getName());
                    from.renameTo(to);
                    Toast.makeText(requireActivity(), "To View Pdf Open File Manager or Internal_Storage/Invoice Generator", Toast.LENGTH_SHORT).show();
                    sharePref.savedData(requireContext(), getString(R.string.invoiceNo_CustomerNameF), invoiceNo + 1);
                    Intent intent = new Intent(requireActivity(), ShowFinalPdf_Activity.class);
                    intent.putExtra("FilePath", to.getAbsolutePath().toString());
                    startActivity(intent);
                } catch (NullPointerException e) {
                    Log.e(TAG, "onClick: ", e);
                    Toast.makeText(requireContext(), "Error " + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showPdf() throws FileNotFoundException {
        try {
            file = createPdfFormIText();

            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(false)
                    .password(null)
                    .enableAntialiasing(true)
                    .spacing(0)
                    .enableDoubletap(true)
                    .load();

        } catch (NullPointerException e) {
            Log.e(TAG, "showPdf: ", e);
            Toast.makeText(requireContext(), "ShowPdf " + e, Toast.LENGTH_LONG).show();
        }

    }

    private File createPdfFormIText() throws FileNotFoundException {

        String customerName = view_model.customerList_ModelView.getValue().get(view_model.currentCustomerSelectedPosition.getValue()).getName();
        StringBuilder stringBuilder = new StringBuilder(customerName);
        for (int i = stringBuilder.length() - 1; i >= 0; i--) {
            if (stringBuilder.charAt(i) == '*' || stringBuilder.charAt(i) == ':' || stringBuilder.charAt(i) == '`' || stringBuilder.charAt(i) == '~')
                stringBuilder.deleteCharAt(i);

        }
        customerName = stringBuilder.toString();

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Invoice Generator/temp");
        if (file.exists()) file.deleteOnExit();
        file.mkdirs();
        invoiceNo = sharePref.getData(requireContext(), getString(R.string.invoiceNo_CustomerNameF), 1);
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh-mm");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String time = simpleTimeFormat.format(date.getTime());
        String dateStr = simpleDateFormat.format(date.getTime());
        String currentDateTimeString = time + " " + dateStr + "(" + invoiceNo + ")";
        String finalPathName = customerName + currentDateTimeString + ".pdf";
        File completePath = new File(file, finalPathName);
        PdfWriter pdfWriter = new PdfWriter(completePath);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A4);
        document.setMargins(72f, 0, 0, 0);


        Table table;

        addCompanyFragmentData(document);

        addCustomerNameFragmentData(document);

        double totalPrice = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            totalPrice = setProductToTable(document);
        }

        addNoteAndFinalPrice(document, totalPrice);

        addSignaturesAndOtherDetails(document, pdfDocument);

        document.close();

        return completePath;

    }

    private void addSignaturesAndOtherDetails( Document document, PdfDocument pdfDocument ) {

        Paragraph for_company_name = new Paragraph("For Company Name").setMargins(20, 80, 0, 30);
        for_company_name.setTextAlignment(TextAlignment.RIGHT);

        Paragraph paragraph = new Paragraph("").setMarginTop(25);

        Paragraph receiver_signature = new Paragraph().add("Receiver Signature").setMargins(10, 100, 0, 80);
        receiver_signature.add(new Tab());
        receiver_signature.addTabStops(new TabStop(800, TabAlignment.RIGHT));
        receiver_signature.add("Signature");
        document.add(for_company_name);
        try {
            Drawable drawable = view_model.signatureDrawable.getValue();
            Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image img = null;
            byte[] byteArray = stream.toByteArray();
            ImageData imageData = ImageDataFactory.createJpeg(byteArray);
            img = new Image(imageData);
            img.setHeight(50);
            img.setMargins(0, 80, 0, 0);
            img.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            document.add(img);
        } catch (Exception e) {

            document.add(paragraph);
        }
        document.add(receiver_signature);

    }

    @SuppressLint("DefaultLocale")
    private void addNoteAndFinalPrice( Document document, double totalPrice ) {

        Table table = new Table(UnitValue.createPointArray(new float[]{5, 2.5f, 2.5f})).useAllAvailableWidth();
        String completeNoteTxt = "*Note : *";
        completeNoteTxt += view_model.selectedNote.getValue().toString().length() == 0 ? "" : view_model.selectedNote.getValue().toString();
        table.addCell(new Cell(3, 1).add(getParagraphWithTextMaker(completeNoteTxt)));


        table.addCell(new Cell().add(getParagraphWithTextMaker("Total Amount")));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(Math.round(totalPrice)))));

        table.addCell(new Cell().add(getParagraphWithTextMaker("Received")));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(Math.round(view_model.receivedAmount.getValue())))));

        table.addCell(new Cell().add(getParagraphWithTextMaker("Balance")));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(Math.round(totalPrice - view_model.receivedAmount.getValue())))));

        table.setMargins(20, 30, 0, 30).setFontSize(12f);
        NumberToWordsConverter converter = new NumberToWordsConverter();
        table.addCell(new Cell(1, 3).add(new Paragraph("Total In Words : " + converter.convert((int) ((Math.round(totalPrice - view_model.receivedAmount.getValue())))))));

        document.add(table);
    }


    private void addCustomerNameFragmentData( Document document ) {
        float[] columnWidth = {8.5f, 2f};
        Table table = new Table(UnitValue.createPointArray(columnWidth)).useAllAvailableWidth();
        Paragraph customerName = new Paragraph();
        Paragraph address = new Paragraph();
        Paragraph gstIn = new Paragraph();
        Paragraph customerState = new Paragraph();

        Paragraph invoiceNo = new Paragraph();
        Paragraph invoiceData = new Paragraph();
        Paragraph compState = new Paragraph();
        Paragraph codeNo = new Paragraph();

        for (Text text : textMaker(customerList_model.getName())) customerName.add(text);
        for (Text text : textMaker(customerList_model.getInvoiceNo())) invoiceNo.add(text);

        for (Text text : textMaker(customerList_model.getAddress())) address.add(text);
        for (Text text : textMaker(customerList_model.getInvoiceDate())) invoiceData.add(text);

        for (Text text : textMaker(customerList_model.getGstIn())) gstIn.add(text);
        for (Text text : textMaker(customerList_model.getCompState())) compState.add(text);

        for (Text text : textMaker(customerList_model.getCustomerState())) customerState.add(text);
        for (Text text : textMaker(customerList_model.getCodeNo())) codeNo.add(text);

        table.addCell(new Cell().add(customerName).setTextAlignment(TextAlignment.LEFT));
        table.addCell(new Cell().add(invoiceNo).setTextAlignment(TextAlignment.LEFT));

        table.addCell(new Cell().add(address).setTextAlignment(TextAlignment.LEFT));
        table.addCell(new Cell().add(invoiceData).setTextAlignment(TextAlignment.LEFT));

        table.addCell(new Cell().add(gstIn).setTextAlignment(TextAlignment.LEFT));
        table.addCell(new Cell().add(compState).setTextAlignment(TextAlignment.LEFT));

        table.addCell(new Cell().add(customerState).setTextAlignment(TextAlignment.LEFT));
        table.addCell(new Cell().add(codeNo).setTextAlignment(TextAlignment.LEFT));

        table.setMargins(20, 30, 0, 30);
        document.add(table);

    }

    private void addCompanyFragmentData( Document document ) {

        addCompanyNameAndInvoiceType(document);
        addCompanyInfo1And2(document);

    }

    private void addCompanyInfo1And2( Document document ) {
        String companyInfo1 = companyFragmentData.get(getString(R.string.companyInfo1_CompanyF));

        document.add(drawLine(ColorConstants.BLACK, 1, new PrintAttributes.Margins(30, 0, 30, 0)));

        ArrayList<Text> companyInfo1TextList = textMaker(companyInfo1);

        Paragraph p = new Paragraph();
        for (Text text : companyInfo1TextList) p.add(text);

        p.setFontSize(12f);
        p.setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER);
        document.add(p);
        document.add(drawLine(ColorConstants.BLACK, 1, new PrintAttributes.Margins(30, 0, 30, 0)));
        String companyInfo2 = companyFragmentData.get(getString(R.string.companyInfo2_CompanyF));

        ArrayList<Text> companyInfo2TextList = textMaker(companyInfo2);
        Paragraph paragraph = new Paragraph();
        for (Text text : companyInfo2TextList) paragraph.add(text);

        paragraph.setFontSize(12f);
        paragraph.setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER);
        document.add(paragraph);
        if (companyInfo2.length() != 0)
            document.add(drawLine(ColorConstants.BLACK, 1, new PrintAttributes.Margins(30, 0, 30, 0)));

    }

    private void addCompanyNameAndInvoiceType( Document document ) {
        String companyName = companyFragmentData.get(getString(R.string.companyName_CompanyF));
        String invoiceType = companyFragmentData.get(getString(R.string.invoiceType_CompanyF));
        Paragraph p = new Paragraph();

        ArrayList<Text> companyNameTextList = textMaker(companyName);
        ArrayList<Text> invoiceTypeTextList = textMaker(invoiceType);

        for (Text text : companyNameTextList) p.add(text.setFontSize(36f));
        p.add(new Tab());
        p.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
        for (Text text : invoiceTypeTextList) p.add(text.setFontSize(24f));
        p.setMargins(0, 30, 0, 30);
        document.add(p);
    }

    private ArrayList<Text> textMaker( String text ) {

        ArrayList<Text> texts = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder(text);

        boolean isStartBold = false;
        boolean isStartItalic = false;
        boolean isStartLineThrough = false;
        boolean isStartUnderLine = false;

        int boldStartPosition = -1;
        int italicStartPosition = -1;
        int lineThroughStartPosition = -1;
        int underLineStartPosition = -1;

        ArrayList<String> makeBoldCharList = new ArrayList<>();
        ArrayList<String> makeItalicCharList = new ArrayList<>();
        ArrayList<String> makeLineThroughCharList = new ArrayList<>();
        ArrayList<String> makeUnderLineCharList = new ArrayList<>();
        ArrayList<String> removableCharactersFormText = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {

            if (stringBuilder.charAt(i) == '*') {
                if (isStartBold) {
                    for (int j = boldStartPosition + 1; j < i; j++) {
                        makeBoldCharList.add(String.valueOf(j));
                    }
                    removableCharactersFormText.add(String.valueOf(i));
                    removableCharactersFormText.add(String.valueOf(boldStartPosition));
                    isStartBold = false;
                    boldStartPosition = -1;
                } else {
                    isStartBold = true;
                    boldStartPosition = i;
                }
            }

            if (stringBuilder.charAt(i) == '_') {
                if (isStartItalic) {
                    for (int j = italicStartPosition + 1; j < i; j++) {
                        makeItalicCharList.add(String.valueOf(j));
                    }
                    removableCharactersFormText.add(String.valueOf(i));
                    removableCharactersFormText.add(String.valueOf(italicStartPosition));
                    isStartItalic = false;
                    italicStartPosition = -1;
                } else {
                    isStartItalic = true;
                    italicStartPosition = i;
                }
            }

            if (stringBuilder.charAt(i) == '~') {
                if (isStartLineThrough) {
                    for (int j = lineThroughStartPosition + 1; j < i; j++) {
                        makeLineThroughCharList.add(String.valueOf(j));
                    }
                    removableCharactersFormText.add(String.valueOf(i));
                    removableCharactersFormText.add(String.valueOf(lineThroughStartPosition));
                    isStartLineThrough = false;
                    lineThroughStartPosition = -1;
                } else {
                    isStartLineThrough = true;
                    lineThroughStartPosition = i;
                }
            }

            if (stringBuilder.charAt(i) == '`') {
                if (isStartUnderLine) {
                    for (int j = underLineStartPosition + 1; j < i; j++) {
                        makeUnderLineCharList.add(String.valueOf(j));
                    }
                    removableCharactersFormText.add(String.valueOf(i));
                    removableCharactersFormText.add(String.valueOf(underLineStartPosition));
                    isStartUnderLine = false;
                    underLineStartPosition = -1;
                } else {
                    isStartUnderLine = true;
                    underLineStartPosition = i;
                }
            }
        }

        for (int i = 0; i < stringBuilder.length(); i++) {
            if (removableCharactersFormText.contains(String.valueOf(i))) continue;
            Text text1 = new Text(String.valueOf(stringBuilder.charAt(i)));

            if (makeBoldCharList.contains(String.valueOf(i))) text1.setBold();

            if (makeItalicCharList.contains(String.valueOf(i))) text1.setItalic();

            if (makeUnderLineCharList.contains(String.valueOf(i))) text1.setUnderline();

            if (makeLineThroughCharList.contains(String.valueOf(i))) text1.setLineThrough();

            texts.add(text1);
        }

        return texts;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private double setProductToTable( Document document ) {
        System.out.println(view_model.selectedNote.getValue() + "   Note");
        ArrayList<ProductList_Model> productList_models = view_model.productList_ModelView.getValue();
        ArrayList<String> selectedProduct = view_model.selectedProductList.getValue();
        HashMap<String, Boolean> checkBoxIsChecked = view_model.isCheckBoxChecked_productF.getValue();
        boolean isDiscountShow = checkBoxIsChecked.getOrDefault("Discount", false);
        boolean isItemCodeShow = checkBoxIsChecked.getOrDefault("ItemCode", false);
        boolean isTaxShow = checkBoxIsChecked.getOrDefault("Tax", false);
        double totalPrice = 0;
        Table table;
        float[] columnWidths = {0.5f, 6, 1, 1, 1};
        int numberOfColumns = 0;
        if (isDiscountShow) {

            numberOfColumns++;
        }
        if (isTaxShow) {
            numberOfColumns++;
        }
        if (isItemCodeShow) {
            numberOfColumns++;
        }
        switch (numberOfColumns) {
            case 0:
                columnWidths = new float[]{0.5f, 6, 1, 1, 1};
                break;
            case 1:
                columnWidths = new float[]{0.5f, 6, 1, 1, 1, 1};
                break;
            case 2:
                columnWidths = new float[]{0.5f, 6, 1, 1, 1, 1, 1};
                break;
            case 3:
                columnWidths = new float[]{0.5f, 6, 1, 1, 1, 1, 1, 1};
                break;
        }
        table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));
        table.useAllAvailableWidth();
        for (int i = -1; i < selectedProduct.size(); i++) {
            table.setFontSize(12f);
            if (i == -1) {
                table.addCell(new Cell().add(new Paragraph("#")).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addCell(new Cell().add(new Paragraph("Item Description")).setWidth(40).setVerticalAlignment(VerticalAlignment.MIDDLE));
                if (isItemCodeShow)
                    table.addCell(new Cell().add(new Paragraph("Code")).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addCell(new Cell().add(new Paragraph("Quantity")).setVerticalAlignment(VerticalAlignment.MIDDLE));
                if (isDiscountShow) {
                    table.addCell(new Cell().add(new Paragraph("Discount")).setVerticalAlignment(VerticalAlignment.MIDDLE));
                }
                if (isTaxShow) {
                    table.addCell(new Cell().add(new Paragraph("Tax")).setVerticalAlignment(VerticalAlignment.MIDDLE));
                }
                table.addCell(new Cell().add(new Paragraph("Rate")).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addCell(new Cell().add(new Paragraph("Amount")).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.setMargins(30, 30, 0, 30).setTextAlignment(TextAlignment.CENTER);

                continue;
            }
            table.startNewRow();
            ProductList_Model productModel = productList_models.get(Integer.parseInt(selectedProduct.get(i)));

            String name = productModel.getProductName();
            String price = productModel.getPrice();
            String quantity = productModel.getQuantity();
            String discount = productModel.getDiscount();
            String tax = productModel.getTax();
            String type = productModel.getType();
            String tax_price = productModel.getTax_price();
            String discount_price = productModel.getDiscount_price();
            String finalPrice = productModel.getFinalPrice();
            String itemCode = productModel.getItemCode();

            finalPrice = finalPrice.substring(9);
            finalPrice = finalPrice.replace(" INR", "");

            totalPrice += Double.parseDouble(finalPrice);

            table.addCell(new Cell().add(getParagraphWithTextMaker(String.valueOf(i + 1))).setVerticalAlignment(VerticalAlignment.MIDDLE));

            table.addCell(new Cell().add(getParagraphWithTextMaker(name)).setVerticalAlignment(VerticalAlignment.MIDDLE));
            if (isItemCodeShow)
                table.addCell(new Cell().add(getParagraphWithTextMaker(itemCode)).setVerticalAlignment(VerticalAlignment.MIDDLE));
            table.addCell(new Cell().add(getParagraphWithTextMaker(quantity + " " + type)).setVerticalAlignment(VerticalAlignment.MIDDLE));
            if (isDiscountShow) {
                table.addCell(new Cell().add(getParagraphWithTextMaker(discount_price +
                        ((discount.length() != 0) ? " (" + discount + "%" + ")" : ""))).setVerticalAlignment(VerticalAlignment.MIDDLE));
            }
            if (isTaxShow) {
                table.addCell(new Cell().add(getParagraphWithTextMaker(tax_price +
                        ((tax.length() != 0) ? " (" + tax + "%" + ")" : "")
                )).setVerticalAlignment(VerticalAlignment.MIDDLE));
            }
            table.addCell(new Cell().add(getParagraphWithTextMaker(price)).setVerticalAlignment(VerticalAlignment.MIDDLE));

            table.addCell(new Cell().add(getParagraphWithTextMaker(finalPrice)).setVerticalAlignment(VerticalAlignment.MIDDLE));

            table.setMargins(0, 30, 0, 30);
            table.setFontSize(11f);
            table.setTextAlignment(TextAlignment.CENTER);
        }
        table.setMargins(20, 30, 0, 30);
        document.add(table);
        return totalPrice;

    }

    private Paragraph getParagraphWithTextMaker( String text ) {
        ArrayList<Text> textArrayList = textMaker(text);
        Paragraph paragraph = new Paragraph();
        for (Text text1 : textArrayList) paragraph.add(text1);
        return paragraph;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Invoice Generator/temp");
        file.mkdirs();
        file.delete();
        super.onStop();
    }

    private LineSeparator drawLine( Color color, float lineWidth, PrintAttributes.Margins margins ) {
        final SolidLine lineDrawer = new SolidLine(lineWidth);
        lineDrawer.setColor(color);
        return new LineSeparator(lineDrawer).setMargins(margins.getTopMils(), margins.getRightMils(), margins.getBottomMils(), margins.getLeftMils());
    }


    public interface PreviewF_interface {
        void closePreview_fragment( int position );

    }

    public static class NumberToWordsConverter {

        public static final String[] units = {"", "One", "Two", "Three", "Four",
                "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
                "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
                "Eighteen", "Nineteen"};

        public static final String[] tens = {
                "",
                "",
                "Twenty",
                "Thirty",
                "Forty",
                "Fifty",
                "Sixty",
                "Seventy",
                "Eighty",
                "Ninety"
        };

        public static String convert( final int n ) {
            if (n < 0) {
                return "Minus " + convert(-n);
            }

            if (n < 20) {
                return units[n];
            }

            if (n < 100) {
                return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
            }

            if (n < 1000) {
                return units[n / 100] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
            }

            if (n < 100000) {
                return convert(n / 1000) + " Thousand" + ((n % 10000 != 0) ? " " : "") + convert(n % 1000);
            }

            if (n < 10000000) {
                return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
            }

            return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
        }
    }
}