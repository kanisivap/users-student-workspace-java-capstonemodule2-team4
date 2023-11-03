package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.LoginResponseDto;
import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TransferDto> getTransfers(String username) {
        List<TransferDto> transfersList = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_desc, t.transfer_type_id, transfer_status_desc, " +
                "t.transfer_status_id, account_from, account_to, amount " +
                "FROM transfer t " +
                "JOIN account a ON a.account_id = t.account_from " +
                "JOIN tenmo_user tu ON tu.user_id = a.user_id " +
                "JOIN account b ON b.account_id = t.account_to " +
                "JOIN tenmo_user tu2 ON tu2.user_id = b.user_id " +
                "JOIN transfer_type tt ON tt.transfer_type_id = t.transfer_type_id " +
                "JOIN transfer_status ts ON ts.transfer_status_id = t.transfer_status_id " +
                "WHERE (tu.username = ? OR tu2.username = ?) AND (t.transfer_status_id = 2 OR t.transfer_status_id = 3);";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, username);
            while (results.next()) {
                TransferDto transfer = mapRowToTransfer(results);
                transfersList.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfersList;
    }

    @Override
    public List<TransferDto> getTransfersByPending(String username) {
        List<TransferDto> transfersList = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_desc, t.transfer_type_id, transfer_status_desc, " +
                "t.transfer_status_id, account_from, account_to, amount " +
                "FROM transfer t " +
                "JOIN transfer_type tt ON tt.transfer_type_id = t.transfer_type_id " +
                "JOIN transfer_status ts ON ts.transfer_status_id = t.transfer_status_id " +
                "JOIN account a ON a.account_id = t.account_from " +
                "JOIN tenmo_user tu ON tu.user_id = a.user_id " +
                "WHERE tu.username = ? AND ts.transfer_status_id = 1;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            while (results.next()) {
                TransferDto transfer = mapRowToTransfer(results);
                transfersList.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfersList;
    }

    @Override
    public TransferDto getTransferById(int transferId, String username) {
        TransferDto transfer = null;
        String sql = "SELECT transfer_id, transfer_type_desc, t.transfer_type_id, transfer_status_desc, " +
                "t.transfer_status_id, account_from, account_to, amount " +
                "FROM transfer t " +
                "JOIN transfer_type tt ON tt.transfer_type_id = t.transfer_type_id " +
                "JOIN transfer_status ts ON ts.transfer_status_id = t.transfer_status_id " +
                "JOIN account a ON a.account_id = t.account_from " +
                "JOIN tenmo_user tu ON tu.user_id = a.user_id " +
                "JOIN account b ON b.account_id = t.account_to " +
                "JOIN tenmo_user tu2 ON tu2.user_id = b.user_id " +
                "WHERE (tu.username = ? OR tu2.username = ?) AND transfer_id = ?;";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, username, transferId);
            if (results.next()) {
                transfer = mapRowToTransfer(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfer;
    }

    @Override
    public TransferDto createTransfer(TransferDto transferDto, String username) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id," +
                "account_from, account_to, amount) VALUES (?,?,(SELECT account_id FROM account WHERE user_id = ?),(SELECT account_id FROM account WHERE user_id = ?),?) " +
                "RETURNING transfer_id;";
        TransferDto result = null;
        try {
            int transferId = jdbcTemplate.queryForObject(sql, int.class, transferDto.getTransferTypeId(), transferDto.getTransferStatusId(),
                    transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount());
            result = getTransferById(transferId, username);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return result;
    }

    private TransferDto mapRowToTransfer(SqlRowSet rs) {
        TransferDto transfer = new TransferDto();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeDesc(rs.getString("transfer_type_desc"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusDesc(rs.getString("transfer_status_desc"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }
}
