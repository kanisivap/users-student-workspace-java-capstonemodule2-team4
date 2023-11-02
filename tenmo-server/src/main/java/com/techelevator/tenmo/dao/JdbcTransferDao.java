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
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TransferDto getTransferById(int transferId) {
        TransferDto transfer = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
            if (results.next()) {
                transfer = mapRowToTransfer(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfer;
    }

    @Override
    public TransferDto createTransfer(TransferDto transferDto) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id," +
                "account_from, account_to, amount) VALUES (?,?,(SELECT account_id FROM account WHERE user_id = ?),(SELECT account_id FROM account WHERE user_id = ?),?) " +
                "RETURNING transfer_id;";
        TransferDto result = null;
        try {
            int transferId = jdbcTemplate.queryForObject(sql, int.class, transferDto.getTransferTypeId(), transferDto.getTransferStatusId(),
                    transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount());
            result = getTransferById(transferId);
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
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }
}
