package com.gxg.ledger;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gxg.ledger.adapter.BookAdapter;
import com.gxg.ledger.dialog.AddBookDialog;
import com.gxg.ledger.model.GiftBook;
import com.gxg.ledger.model.GiftRecord;
import com.gxg.ledger.repository.GiftRecordRepository;
import com.gxg.ledger.utils.ImportExportManager;
import com.gxg.ledger.viewmodel.MainViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    // private ActivityMainBinding binding;
    private androidx.appcompat.widget.Toolbar toolbar;
    private androidx.recyclerview.widget.RecyclerView recyclerViewBooks;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabAddBook;
    private com.google.android.material.textfield.TextInputEditText editTextSearch;
    private android.widget.LinearLayout layoutEmpty;
    private MainViewModel viewModel;
    private BookAdapter adapter;
    private List<GiftBook> bookList;
    private GiftRecordRepository recordRepository;
    private GiftBook selectedBookForImport;
    
    // 文件选择器
    private ActivityResultLauncher<String[]> filePickerLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        toolbar = findViewById(R.id.toolbar);
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        fabAddBook = findViewById(R.id.fabAddBook);
        editTextSearch = findViewById(R.id.editTextSearch);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        
        // 暂时注释掉ActionBar设置以排除问题
        /*
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        */
        
        initViews();
        setupRecyclerView();
        setupObservers();
        setupListeners();
    }
    
    private void initViews() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        recordRepository = new GiftRecordRepository(getApplication());
        bookList = new ArrayList<>();
        adapter = new BookAdapter(bookList, this, recordRepository);
        
        // 初始化文件选择器
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    performImport(uri);
                }
            }
        );
    }
    
    private void setupRecyclerView() {
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBooks.setAdapter(adapter);
    }
    
    private void setupObservers() {
        viewModel.getAllBooks().observe(this, books -> {
            bookList.clear();
            if (books != null) {
                bookList.addAll(books);
            }
            adapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }
    
    private void setupListeners() {
        fabAddBook.setOnClickListener(v -> showAddBookDialog());
        
        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks(s.toString());
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    
    private void filterBooks(String query) {
        if (query.isEmpty()) {
            viewModel.getAllBooks().observe(this, books -> {
                bookList.clear();
                if (books != null) {
                    bookList.addAll(books);
                }
                adapter.notifyDataSetChanged();
            });
        } else {
            List<GiftBook> filteredBooks = viewModel.searchBooks(query);
            bookList.clear();
            bookList.addAll(filteredBooks);
            adapter.notifyDataSetChanged();
        }
        updateEmptyState();
    }
    
    private void showAddBookDialog() {
        AddBookDialog dialog = new AddBookDialog(this, new AddBookDialog.OnBookSavedListener() {
            @Override
            public void onBookSaved(GiftBook book) {
                viewModel.insert(book);
                Toast.makeText(MainActivity.this, R.string.operation_success, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onBookUpdated(GiftBook book) {
                viewModel.update(book);
                Toast.makeText(MainActivity.this, R.string.operation_success, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    
    private void updateEmptyState() {
        if (bookList.isEmpty()) {
            recyclerViewBooks.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewBooks.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_import) {
            // 导入功能
            showImportDialog();
            return true;
        } else if (id == R.id.menu_export) {
            // 导出功能
            showExportDialog();
            return true;
        } else if (id == R.id.menu_settings) {
            // 设置功能
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_about) {
            // 关于功能
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("关于")
                .setMessage("礼金簿 v1.0\n\n一个简单易用的礼金管理工具")
                .setPositiveButton("确定", null)
                .show();
    }
    
    private void showImportDialog() {
        // 显示礼金簿选择对话框
        if (bookList.isEmpty()) {
            Toast.makeText(this, "请先创建礼金簿", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] bookNames = new String[bookList.size()];
        for (int i = 0; i < bookList.size(); i++) {
            bookNames[i] = bookList.get(i).getName();
        }
        
        new AlertDialog.Builder(this)
                .setTitle("选择要导入的礼金簿")
                .setItems(bookNames, (dialog, which) -> {
                    selectedBookForImport = bookList.get(which);
                    // 启动文件选择器
                    filePickerLauncher.launch(new String[]{"text/csv", "text/comma-separated-values"});
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void showExportDialog() {
        // 显示礼金簿选择对话框
        if (bookList.isEmpty()) {
            Toast.makeText(this, "请先创建礼金簿", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] bookNames = new String[bookList.size()];
        for (int i = 0; i < bookList.size(); i++) {
            bookNames[i] = bookList.get(i).getName();
        }
        
        new AlertDialog.Builder(this)
                .setTitle("选择要导出的礼金簿")
                .setItems(bookNames, (dialog, which) -> {
                    GiftBook selectedBook = bookList.get(which);
                    performExport(selectedBook);
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void performImport(android.net.Uri uri) {
        if (selectedBookForImport == null) return;
        
        // 显示加载对话框
        AlertDialog loadingDialog = new AlertDialog.Builder(this)
                .setTitle("正在导入...")
                .setMessage("请稍候")
                .setCancelable(false)
                .show();
        
        ImportExportManager.importBookData(this, uri, selectedBookForImport.getId(), 
            recordRepository, new ImportExportManager.ImportCallback() {
                @Override
                public void onSuccess(int importedCount) {
                    runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        Toast.makeText(MainActivity.this, 
                            getString(R.string.import_success, importedCount), 
                            Toast.LENGTH_LONG).show();
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        Toast.makeText(MainActivity.this, 
                            getString(R.string.import_failed, error), 
                            Toast.LENGTH_LONG).show();
                    });
                }
            });
    }
    
    private void performExport(GiftBook book) {
        // 显示加载对话框
        AlertDialog loadingDialog = new AlertDialog.Builder(this)
                .setTitle("正在导出...")
                .setMessage("请稍候")
                .setCancelable(false)
                .show();
        
        // 异步获取数据并导出
        new Thread(() -> {
            try {
                // 获取该礼金簿的所有记录
                List<GiftRecord> records = recordRepository.getRecordsByBookIdSync(book.getId());
                
                runOnUiThread(() -> {
                    ImportExportManager.exportBookData(this, book, records, 
                        new ImportExportManager.ExportCallback() {
                            @Override
                            public void onSuccess(File file) {
                                loadingDialog.dismiss();
                                Toast.makeText(MainActivity.this, 
                                    getString(R.string.export_success) + "\n文件: " + file.getName(), 
                                    Toast.LENGTH_LONG).show();
                            }
                            
                            @Override
                            public void onError(String error) {
                                loadingDialog.dismiss();
                                Toast.makeText(MainActivity.this, 
                                    getString(R.string.export_failed, error), 
                                    Toast.LENGTH_LONG).show();
                            }
                        });
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, 
                        getString(R.string.export_failed, e.getMessage()), 
                        Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
    
    @Override
    public void onBookClick(GiftBook book) {
        // 点击礼金簿，跳转到详情页面
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("book_id", book.getId());
        intent.putExtra("book_name", book.getName());
        startActivity(intent);
    }
    
    @Override
    public void onBookLongClick(GiftBook book) {
        // 长按礼金簿，显示操作菜单
        showBookOptionsDialog(book);
    }
    
    private void showBookOptionsDialog(GiftBook book) {
        String[] options = {"编辑", "删除"};
        new AlertDialog.Builder(this)
                .setTitle("选择操作")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // 编辑
                            showEditBookDialog(book);
                            break;
                        case 1: // 删除
                            showDeleteConfirmDialog(book);
                            break;
                    }
                })
                .show();
    }
    
    private void showEditBookDialog(GiftBook book) {
        AddBookDialog dialog = new AddBookDialog(this, book, new AddBookDialog.OnBookSavedListener() {
            @Override
            public void onBookSaved(GiftBook book) {
                // 不会调用
            }
            
            @Override
            public void onBookUpdated(GiftBook book) {
                viewModel.update(book);
                Toast.makeText(MainActivity.this, R.string.operation_success, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    
    private void showDeleteConfirmDialog(GiftBook book) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除礼金簿 \"" + book.getName() + "\" 吗？此操作将删除所有相关记录。")
                .setPositiveButton("删除", (dialog, which) -> {
                    viewModel.delete(book);
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}