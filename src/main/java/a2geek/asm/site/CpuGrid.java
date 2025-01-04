package a2geek.asm.site;

import a2geek.asm.definition.AddressModeDefinition;
import a2geek.asm.definition.CpuDefinition;
import a2geek.asm.definition.Operation;
import a2geek.asm.definition.OperationAddressing;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the CPU grid for documentation without impacting any operational components of the Assembler.
 * 
 * @author Rob
 */
public class CpuGrid {
	private CpuDefinition cpu;
	
	public CpuGrid(CpuDefinition cpu) {
		this.cpu = cpu;
	}

	/** Defines all headers. */
	public List<String> getHeaders() {
		List<String> headers = new ArrayList<>();
		headers.add("Mnemonic");
		headers.add("Description");
		for (AddressModeDefinition defn : cpu.getAddressModes()) {
			if (defn.getTitle() == null || defn.getTitle().isEmpty()) {
				headers.add(defn.getFormat());
			} else if (defn.getFormat() == null || defn.getFormat().isEmpty()) {
				headers.add(defn.getTitle());
			} else {
				headers.add(String.format("%s &mdash; %s", defn.getTitle(), defn.getFormat()));
			}
		}
		return headers;
	}
	
	/** Retrieves all rows (one per operation). */
	public List<Row> getRows() {
		List<Row> rows = new ArrayList<>();
		for (Operation op : cpu.getOperations()) {
			rows.add(new Row(cpu, op));
		}
		return rows;
	}
	
	/** Defines all row attributes. */
	public static class Row {
		private CpuDefinition cpu;
		private Operation op;
		
		public Row(CpuDefinition cpu, Operation op) {
			this.cpu = cpu;
			this.op = op;
		}
		
		/** Defines each detail column.  Note that this must be in sync with the getHeaders method. */
		public List<String> getColumns() {
			List<String> columns = new ArrayList<>();
			columns.add(op.getMnemonic());
			columns.add(op.getDescription());
			for (AddressModeDefinition defn : cpu.getAddressModes()) {
				OperationAddressing opaddr = op.findOperationAddressingById(defn.getId());
				if (opaddr != null) {
					columns.add(opaddr.getOpcode());
				} else {
					columns.add("-");
				}
			}
			return columns;
		}
	}
}
