package com.example.repairapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<RepairOrder> orders;
    private OnItemClickListener listener;
    private String userRole;

    public interface OnItemClickListener {
        void onItemClick(RepairOrder order);
        void onDeleteClick(RepairOrder order);
    }

    public OrderAdapter(List<RepairOrder> orders, OnItemClickListener listener, String userRole) {
        this.orders = orders;
        this.listener = listener;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view, userRole);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        RepairOrder order = orders.get(position);
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<RepairOrder> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId;
        private TextView tvStatus;
        private TextView tvDeviceInfo;
        private TextView tvClientInfo;
        private TextView tvDates;
        private TextView tvDescription;
        private ImageButton btnDelete;
        private String userRole;

        public OrderViewHolder(@NonNull View itemView, String userRole) {
            super(itemView);
            this.userRole = userRole;

            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDeviceInfo = itemView.findViewById(R.id.tvDeviceInfo);
            tvClientInfo = itemView.findViewById(R.id.tvClientInfo);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            if (!"ADMIN".equals(userRole)) {
                btnDelete.setVisibility(View.GONE);
            }
        }

        public void bind(final RepairOrder order, final OnItemClickListener listener) {
            tvOrderId.setText("Заявка #" + (order.getId() != null ? order.getId() : ""));

            String statusText;
            int statusColor;

            if (order.getStatus() != null) {
                switch (order.getStatus()) {
                    case "NEW":
                        statusText = "НОВАЯ";
                        statusColor = Color.parseColor("#FF9800");
                        break;
                    case "ACCEPTED":
                        statusText = "ПРИНЯТА";
                        statusColor = Color.parseColor("#2196F3");
                        break;
                    case "COMPLETED":
                        statusText = "ВЫПОЛНЕНА";
                        statusColor = Color.parseColor("#4CAF50");
                        break;
                    case "REJECTED":
                        statusText = "ОТКЛОНЕНА";
                        statusColor = Color.parseColor("#F44336");
                        break;
                    default:
                        statusText = order.getStatus();
                        statusColor = Color.parseColor("#9E9E9E");
                }
            } else {
                statusText = "НЕИЗВЕСТНО";
                statusColor = Color.GRAY;
            }

            tvStatus.setText(statusText);
            tvStatus.setBackgroundColor(statusColor);

            String deviceType = order.getDeviceType() != null ? order.getDeviceType() : "";
            String deviceModel = order.getDeviceModel() != null ? order.getDeviceModel() : "";
            tvDeviceInfo.setText("Устройство: " + deviceType + " " + deviceModel);

            String clientName = order.getClientName() != null ? order.getClientName() : "";
            String clientPhone = order.getClientPhone() != null ? order.getClientPhone() : "";
            tvClientInfo.setText("Клиент: " + clientName + " (" + clientPhone + ")");

            String creationDate = order.getCreationDate() != null ? order.getCreationDate() : "";
            String deadline = order.getDeadline() != null ? order.getDeadline() : "";
            tvDates.setText("Создана: " + creationDate + " | Срок: " + deadline);

            String description = order.getProblemDescription();
            if (description != null && description.length() > 100) {
                description = description.substring(0, 100) + "...";
            }
            tvDescription.setText("Проблема: " + (description != null ? description : ""));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(order);
                    }
                }
            });

            if (btnDelete.getVisibility() == View.VISIBLE) {
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onDeleteClick(order);
                        }
                    }
                });
            }
        }
    }
}
