package com.gxg.ledger;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import com.gxg.ledger.adapter.GiftRecordAdapter;
import com.gxg.ledger.dialog.AddRecordDialog;
import com.gxg.ledger.dialog.AdvancedSearchDialog;
import com.gxg.ledger.dialog.MarkReturnDialog;
import com.gxg.ledger.model.GiftBook;
import com.gxg.ledger.model.GiftRecord;
import com.gxg.ledger.model.SearchCriteria;
import com.gxg.ledger.repository.GiftRecordRepository;
import com.gxg.ledger.utils.ImportExportManager;
import com.gxg.ledger.viewmodel.BookDetailViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity implements GiftRecordAdapter.OnRecordClickListener {
    private androidx.appcompat.widget.Toolbar toolbar;
    private TextInputEditText editTextSearch;
    private MaterialButton buttonAddRecord;
    private MaterialButton buttonAdvancedSearch;
    private MaterialButton buttonImport;
    private MaterialButton buttonExport;
    private RecyclerView recyclerViewRecords;
    private android.widget.LinearLayout layoutEmpty;
    private android.widget.TextView textViewRecordCount;
    private android.widget.TextView textViewTotalAmount;
    private android.widget.TextView textViewReturnAmount;
    
    private BookDetailViewModel viewModel;
    private GiftRecordAdapter adapter;
    private List<GiftRecord> recordList;
    private GiftBook currentBook;
    private GiftRecordRepository recordRepository;
    
    // 搜索相关
    private SearchCriteria searchCriteria;
    private boolean isSearching = false;
    
    // 文件选择器
    private ActivityResultLauncher<String[]> filePickerLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        
        initViews();
        getIntentData();
        setupToolbar();
        setupRecyclerView();
        setupObservers();
        setupListeners();
        loadRecords();  // 添加这行来加载宾客记录
        loadStatistics();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonAddRecord = findViewById(R.id.buttonAddRecord);
        buttonAdvancedSearch = findViewById(R.id.buttonAdvancedSearch);
        buttonImport = findViewById(R.id.buttonImport);
        buttonExport = findViewById(R.id.buttonExport);
        recyclerViewRecords = findViewById(R.id.recyclerViewRecords);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        textViewRecordCount = findViewById(R.id.textViewRecordCount);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        textViewReturnAmount = findViewById(R.id.textViewReturnAmount);
        
        viewModel = new ViewModelProvider(this).get(BookDetailViewModel.class);
        recordRepository = new GiftRecordRepository(getApplication());
        recordList = new ArrayList<>();
        adapter = new GiftRecordAdapter(recordList, this);
        searchCriteria = new SearchCriteria();
        
        // 初始化文件选择器
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null && currentBook != null) {
                    performImport(uri);
                }
            }
        );
    }
    
    private void getIntentData() {
        int bookId = getIntent().getIntExtra("book_id", -1);
        String bookName = getIntent().getStringExtra("book_name");
        
        Log.d("BookDetailActivity", "Received bookId: " + bookId + ", bookName: " + bookName);
        
        if (bookId != -1) {
            currentBook = new GiftBook(bookName, "");
            currentBook.setId(bookId);
            viewModel.setBookId(bookId);
            Log.d("BookDetailActivity", "Set viewModel bookId to: " + bookId);
            if (toolbar != null) {
                toolbar.setTitle(bookName);
            }
        } else {
            Log.e("BookDetailActivity", "Invalid bookId received!");
        }
    }
    
    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                // 设置菜单
                toolbar.inflateMenu(R.menu.book_detail_menu);
                toolbar.setOnMenuItemClickListener(item -> {
                    return onOptionsItemSelected(item);
                });
            }
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }
    
    private void setupRecyclerView() {
        recyclerViewRecords.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecords.setAdapter(adapter);
    }
    
    private void setupObservers() {
        // 这里会在loadRecords中处理数据观察
    }
    
    private void setupListeners() {
        buttonAddRecord.setOnClickListener(v -> showAddRecordDialog());
        buttonAdvancedSearch.setOnClickListener(v -> showAdvancedSearchDialog());
        buttonImport.setOnClickListener(v -> showImportDialog());
        buttonExport.setOnClickListener(v -> showExportDialog());
        
        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    // 如果没有文本搜索，但可能有其他筛选条件
                    if (searchCriteria.isEmpty()) {
                        isSearching = false;
                        loadRecords();
                    } else {
                        performAdvancedSearch();
                    }
                } else {
                    // 文本搜索优先
                    searchCriteria.setKeyword(query);
                    isSearching = true;
                    performAdvancedSearch();
                }
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    
    private void loadRecords() {
        // 使用异步方式加载记录
        new Thread(() -> {
            List<GiftRecord> records = viewModel.getRecords();
            Log.d("BookDetailActivity", "Loaded records count: " + records.size());
            for (GiftRecord record : records) {
                Log.d("BookDetailActivity", "Record: " + record.getPersonName() + ", BookId: " + record.getBookId());
            }
            runOnUiThread(() -> {
                recordList.clear();
                recordList.addAll(records);
                Log.d("BookDetailActivity", "recordList size after update: " + recordList.size());
                adapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }).start();
    }
    
    private void loadStatistics() {
        if (currentBook != null) {
            // 使用异步方式加载统计数据
            new Thread(() -> {
                int count = viewModel.getRecordCount();
                Double total = viewModel.getTotalAmount();
                Double returned = viewModel.getReturnedAmount();
                
                // 在主线程更新UI
                runOnUiThread(() -> {
                    textViewRecordCount.setText(String.valueOf(count));
                    textViewTotalAmount.setText(formatCurrency(total));
                    textViewReturnAmount.setText(formatCurrency(returned));
                });
            }).start();
        }
    }
    
    private String formatCurrency(Double amount) {
        if (amount == null) return "¥0.00";
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "¥" + df.format(amount);
    }
    
    private void filterRecords(String query) {
        if (query.isEmpty()) {
            loadRecords();
        } else {
            // 异步搜索
            new Thread(() -> {
                List<GiftRecord> filteredRecords = viewModel.searchRecords(query);
                runOnUiThread(() -> {
                    recordList.clear();
                    recordList.addAll(filteredRecords);
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
            }).start();
        }
    }
    
    private void performAdvancedSearch() {
        if (currentBook == null) return;
        
        new Thread(() -> {
            try {
                List<GiftRecord> results;
                if (searchCriteria.isEmpty()) {
                    // 无条件，加载所有记录
                    results = viewModel.getRecords();
                } else {
                    // 执行高级搜索
                    results = performSearchWithCriteria(searchCriteria);
                }
                
                final List<GiftRecord> finalResults = results;
                runOnUiThread(() -> {
                    recordList.clear();
                    recordList.addAll(finalResults);
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(BookDetailActivity.this, "搜索出错: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private List<GiftRecord> performSearchWithCriteria(SearchCriteria criteria) {
        List<GiftRecord> allRecords = viewModel.getRecords();
        List<GiftRecord> filteredRecords = new ArrayList<>();
        
        for (GiftRecord record : allRecords) {
            boolean matches = true;
            
            // 关键词搜索
            if (!criteria.getKeyword().isEmpty()) {
                String keyword = criteria.getKeyword().toLowerCase();
                boolean keywordMatch = record.getPersonName().toLowerCase().contains(keyword) ||
                                     (record.getNotes() != null && record.getNotes().toLowerCase().contains(keyword));
                if (!keywordMatch) matches = false;
            }
            
            // 地址筛选
            if (!criteria.getAddress().isEmpty() && matches) {
                String address = criteria.getAddress().toLowerCase();
                if (record.getAddress() == null || !record.getAddress().toLowerCase().contains(address)) {
                    matches = false;
                }
            }
            
            // 还礼状态筛选
            if (criteria.getReturned() != null && matches) {
                if (record.isReturned() != criteria.getReturned()) {
                    matches = false;
                }
            }
            
            // 金额筛选
            if ((criteria.getMinAmount() != null || criteria.getMaxAmount() != null) && matches) {
                double amount = record.getAmount();
                if (criteria.getMinAmount() != null && amount < criteria.getMinAmount()) {
                    matches = false;
                }
                if (criteria.getMaxAmount() != null && amount > criteria.getMaxAmount()) {
                    matches = false;
                }
            }
            
            // 日期筛选
            if ((criteria.getStartDate() != null || criteria.getEndDate() != null) && matches) {
                long eventDate = record.getEventDate();
                if (criteria.getStartDate() != null && eventDate < criteria.getStartDate()) {
                    matches = false;
                }
                if (criteria.getEndDate() != null && eventDate > criteria.getEndDate()) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredRecords.add(record);
            }
        }
        
        return filteredRecords;
    }
    
    private void showAddRecordDialog() {
        AddRecordDialog dialog = new AddRecordDialog(this, new AddRecordDialog.OnRecordSavedListener() {
            @Override
            public void onRecordSaved(GiftRecord record) {
                // 设置礼金簿ID
                record.setBookId(currentBook.getId());
                viewModel.insertRecord(record);
                // 延迟刷新，确保数据库操作完成
                new android.os.Handler().postDelayed(() -> {
                    loadRecords();
                    loadStatistics();
                    Toast.makeText(BookDetailActivity.this, R.string.operation_success, Toast.LENGTH_SHORT).show();
                }, 300);
            }
            
            @Override
            public void onRecordUpdated(GiftRecord record) {
                // 不会调用
            }
        });
        dialog.show();
    }
    
    private void updateEmptyState() {
        if (recordList.isEmpty()) {
            recyclerViewRecords.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewRecords.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onRecordClick(GiftRecord record) {
        // 点击记录显示详情或编辑
        showEditRecordDialog(record);
    }
    
    @Override
    public void onRecordLongClick(GiftRecord record) {
        // 长按记录显示操作菜单
        showRecordOptionsDialog(record);
    }
    
    @Override
    public void onMarkReturnClick(GiftRecord record) {
        // 点击还礼按钮
        showMarkReturnDialog(record);
    }
    
    @Override
    public void onEditClick(GiftRecord record) {
        // 点击编辑按钮
        showEditRecordDialog(record);
    }
    
    @Override
    public void onDeleteClick(GiftRecord record) {
        // 点击删除按钮
        showDeleteRecordConfirmDialog(record);
    }
    
    private void showRecordOptionsDialog(GiftRecord record) {
        String[] options = {getContext().getString(R.string.edit_record), getContext().getString(R.string.delete_record)};
        new AlertDialog.Builder(this)
                .setTitle(getContext().getString(R.string.select_operation))
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // 编辑
                            showEditRecordDialog(record);
                            break;
                        case 1: // 删除
                            showDeleteRecordConfirmDialog(record);
                            break;
                    }
                })
                .show();
    }
    
    private void showEditRecordDialog(GiftRecord record) {
        AddRecordDialog dialog = new AddRecordDialog(this, record, new AddRecordDialog.OnRecordSavedListener() {
            @Override
            public void onRecordSaved(GiftRecord record) {
                // 不会调用
            }
            
            @Override
            public void onRecordUpdated(GiftRecord record) {
                viewModel.updateRecord(record);
                // 延迟刷新，确保数据库操作完成
                new android.os.Handler().postDelayed(() -> {
                    loadRecords();
                    loadStatistics();
                    Toast.makeText(BookDetailActivity.this, R.string.operation_success, Toast.LENGTH_SHORT).show();
                }, 300);
            }
        });
        dialog.show();
    }
    
    private void showDeleteRecordConfirmDialog(GiftRecord record) {
        new AlertDialog.Builder(this)
                .setTitle(getContext().getString(R.string.confirm_delete))
                .setMessage(getContext().getString(R.string.delete_record_confirm, record.getPersonName()))
                .setPositiveButton(getContext().getString(R.string.delete), (dialog, which) -> {
                    // 先执行删除操作
                    viewModel.deleteRecord(record);
                    
                    // 延迟刷新，确保数据库操作完成
                    new android.os.Handler().postDelayed(() -> {
                        loadRecords();
                        loadStatistics();
                        Toast.makeText(this, getContext().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    }, 300);
                })
                .setNegativeButton(getContext().getString(R.string.cancel), null)
                .show();
    }
    
    private void showMarkReturnDialog(GiftRecord record) {
        MarkReturnDialog dialog = new MarkReturnDialog(this, record, new MarkReturnDialog.OnReturnMarkedListener() {
            @Override
            public void onReturnMarked(GiftRecord record) {
                viewModel.updateRecord(record);
                // 延迟刷新，确保数据库操作完成
                new android.os.Handler().postDelayed(() -> {
                    loadRecords();
                    loadStatistics();
                    Toast.makeText(BookDetailActivity.this, R.string.operation_success, Toast.LENGTH_SHORT).show();
                }, 300);
            }
        });
        dialog.show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menu_import) {
            showImportDialog();
            return true;
        } else if (id == R.id.menu_export) {
            showExportDialog();
            return true;
        } else if (id == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_about) {
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
        if (currentBook == null) {
            Toast.makeText(this, "无法获取当前礼金簿信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 直接启动文件选择器
        filePickerLauncher.launch(new String[]{"text/csv", "text/comma-separated-values"});
    }
    
    private void showExportDialog() {
        if (currentBook == null) {
            Toast.makeText(this, "无法获取当前礼金簿信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 直接导出当前礼金簿
        performExport(currentBook);
    }
    
    private void showAdvancedSearchDialog() {
        if (currentBook == null) {
            Toast.makeText(this, "请先进入礼金簿", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(searchCriteria, 
            new AdvancedSearchDialog.OnSearchConfirmedListener() {
                @Override
                public void onSearchConfirmed(SearchCriteria criteria) {
                    searchCriteria = criteria;
                    isSearching = !criteria.isEmpty();
                    performAdvancedSearch();
                }
                
                @Override
                public void onSearchCancelled() {
                    // 取消搜索，恢复原始状态
                    searchCriteria.clear();
                    isSearching = false;
                    editTextSearch.setText("");
                    loadRecords();
                }
            });
        dialog.show(getSupportFragmentManager(), "advanced_search_dialog");
    }
    
    private void performImport(android.net.Uri uri) {
        if (currentBook == null) return;
        
        // 显示加载对话框
        AlertDialog loadingDialog = new AlertDialog.Builder(this)
                .setTitle("正在导入...")
                .setMessage("请稍候")
                .setCancelable(false)
                .show();
        
        ImportExportManager.importBookData(this, uri, currentBook.getId(), 
            recordRepository, new ImportExportManager.ImportCallback() {
                @Override
                public void onSuccess(int importedCount) {
                    runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        Toast.makeText(BookDetailActivity.this, 
                            getString(R.string.import_success, importedCount), 
                            Toast.LENGTH_LONG).show();
                        // 延迟刷新，确保数据库操作完成
                        new android.os.Handler().postDelayed(() -> {
                            loadRecords();
                            loadStatistics();
                        }, 500);
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        Toast.makeText(BookDetailActivity.this, 
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
                                Toast.makeText(BookDetailActivity.this, 
                                    getString(R.string.export_success) + "\n文件: " + file.getName(), 
                                    Toast.LENGTH_LONG).show();
                            }
                            
                            @Override
                            public void onError(String error) {
                                loadingDialog.dismiss();
                                Toast.makeText(BookDetailActivity.this, 
                                    getString(R.string.export_failed, error), 
                                    Toast.LENGTH_LONG).show();
                            }
                        });
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(BookDetailActivity.this, 
                        getString(R.string.export_failed, e.getMessage()), 
                        Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
    
    private Context getContext() {
        return this;
    }
}