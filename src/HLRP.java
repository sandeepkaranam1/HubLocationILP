import java.util.ArrayList;
import java.util.Arrays;

import net.sf.javailp.*;

public class HLRP {

	public static void main(String[] args) {
		
		SolverFactory factory = new SolverFactoryLpSolve(); // use lp_solve
		factory.setParameter(Solver.VERBOSE, 0);
		factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds
		int total_nodes = 10;
		Problem NewProblem = new Problem();
		// parameters set up
		double[] y = { 1, 0, 0, 0, 0, 0, 0, 1, 0, 0 }; // facility at location i
														// is open
														// if y[i]=1; otherwise
														// 0;
		double[] d = { 0, 20, 40, 50, 70, 0, 0, 0, 0, 0 }; // demand at client i
															// is d[i]
		double[] fh = { 2000, 3000, 4000, 100, 300, 100, 200, 0, 50, 0 }; // cost
																			// of
																			// opening
																			// hub
																			// at
		// location i is fh[i]
		double[][] t = { { 0, 5, 5, 1, 1, 2, 2, 1, 3, 3 }, { 5, 0, 5, 3, 1, 1, 2, 1, 3, 3 },
				{ 5, 5, 0, 1, 3, 1, 2, 2, 3, 3 }, { 1, 3, 1, 0, 1, 1, 6, 2, 3, 3 }, { 1, 1, 3, 1, 4, 3, 1, 3, 2, 3 },
				{ 0, 5, 5, 1, 1, 2, 2, 1, 3, 3 }, { 0, 5, 5, 1, 1, 2, 2, 1, 3, 3 }, { 0, 5, 5, 1, 1, 2, 2, 1, 3, 3 },
				{ 0, 5, 5, 1, 1, 2, 2, 1, 3, 3 }, { 0, 5, 5, 1, 1, 2, 2, 1, 3, 3 } } // demand
																						// on
		; // cost of routing
			// one unit of
			// arc(u,v)
		double[] ch = { 10, 10, 10, 50, 50, 50, 30, 0, 50, 0 }; // capacity at
																// hub i is
																// ch[i]
		double[] b = { 90, 0, 0, 0, 0, 0, 0, 90, 0, 0 }; // capacity at
																	// facility
																	// i is b[i]
		double[][] k = { { 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 }, { 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 },
				{ 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 }, { 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 },
				{ 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 }, { 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 },
				{ 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 }, { 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 },
				{ 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 }, { 40, 40, 40, 40, 40, 40, 40, 40, 40, 40 } }; // capacity
																											// on
		// arc(u,v) is
		// k[u][v]
		ArrayList<Integer> clients = new ArrayList<>(Arrays.asList( 2,3,4,5));
		ArrayList<Integer> facilities = new ArrayList<>(Arrays.asList(1,8));
		
		///****************** Input Data Ends here************///

		for (int i = 1; i <= total_nodes; i++) {

		}
		// variables set up
		for (int i = 1; i <= total_nodes; i++) {
			for (int j = 1; j <= total_nodes; j++) {
				NewProblem.setVarType("x" + i + j, VarType.INT);
				NewProblem.setVarType("z" + i + j, VarType.BOOL);

			}
		}

		for (int i = 1; i <= total_nodes; i++) {
			for (int j = 1; j <= total_nodes; j++) {
				for (int m = 1; m <= total_nodes; m++) {
					for (int n = 1; n <= total_nodes; n++) {

						NewProblem.setVarType("f" + i + "(" + m + "," + n + ")" + j, VarType.INT);
					}
				}

			}
		}

		// objective
		Linear linear = new Linear();
		for (int i = 1; i <= total_nodes; i++) {
			for (int j = 1; j <= total_nodes; j++) {
				linear.add(t[i - 1][j - 1] * d[i - 1], "z" + i + j);
			}
			linear.add(fh[i - 1], "z" + i + i);
		}

		for (int i = 1; i <= total_nodes; i++) {
			for (int j = 1; j <= total_nodes; j++) {
				for (int m = 1; m <= total_nodes; m++) {
					for (int n = 1; n <= total_nodes; n++) {
						linear.add(t[m - 1][n - 1], "f" + i + "(" + m + "," + n + ")" + j);
					}
				}

			}
		}
		NewProblem.setObjective(linear, OptType.MIN);

		// Constraints
		for (int i = 0; i < clients.size(); i++) {
			linear = new Linear();
			for (int j = 1; j <= total_nodes; j++) {
				linear.add(1, "z" + clients.get(i) + j);
			}
			NewProblem.add(linear, "=", 1);
		}

		// 2
		for (int i = 1; i <= total_nodes; i++) {
			for (int j = 1; j <= total_nodes; j++) {
				if (i != j) {
					linear = new Linear();
					linear.add(1, "z" + i + j);
					linear.add(-1, "z" + j + j);
					NewProblem.add(linear, "<=", 0);

				}

			}
		}

		// 3
		for (int i = 1; i <= total_nodes; i++) {
			linear = new Linear();
			
			linear.add(1, "z" + i + i);
			NewProblem.add(linear, "<=", 1-y[i-1]);
		}

		for (int i = 1; i <= total_nodes; i++) {
			linear = new Linear();
			for (int j = 1; j <= total_nodes; j++) {
				linear.add(d[j - 1], "z" + j + i);
				linear.add(-1, "x" + i + j);
			}
			NewProblem.add(linear, "=", 0);
		}
		// 4
		for (int i = 1; i <= total_nodes; i++) {
			linear = new Linear();
			for (int j = 1; j <= total_nodes; j++) {
				linear.add(d[j - 1], "z" + j + i);
			}
			linear.add(-ch[i - 1], "z" + i + i);
			NewProblem.add(linear, "<=", 1);
		}

		// 5
		for (int i = 1; i <= total_nodes; i++) {
			linear = new Linear();
			for (int j = 1; j <= total_nodes; j++) {
				linear.add(1, "x" + j + i);
			}
			NewProblem.add(linear, "<=", b[i - 1] * y[i - 1]);
		}

		// 6
		for (int i = 1; i <= total_nodes; i++) {
			for (int j = 1; j <= total_nodes; j++) {
				for (int m = 1; m <= total_nodes; m++) {
					for (int n = 1; n <= total_nodes; n++) {
						linear = new Linear();
						linear.add(-1, "f" + i + "(" + m + "," + n + ")" + j);
						linear.add(-1, "x" + i + j);
						NewProblem.add(linear, "<=", 0);
					}
				}

			}
		}

		// 7
		for (int i = 1; i <= total_nodes; i++) {
			for (int j = 1; j <= total_nodes; j++) {
				linear = new Linear();
				for (int m = 1; m <= total_nodes; m++) {
					for (int n = 1; n <= total_nodes; n++) {
						linear.add(1, "f" + m + "(" + i + "," + j + ")" + n);
					}
				}
				NewProblem.add(linear, "<=", k[i - 1][j - 1]);

			}
		}

		// 8
		for (int i = 1; i <= total_nodes; i++) {
			for (int j = 1; j <= total_nodes; j++) {
				linear = new Linear();
				if (i != j) {
					for (int m = 1; m <= total_nodes; m++) {
						for (int n = 1; n <= total_nodes; n++) {
							linear.add(1, "f" + i + "(" + m + "," + j + ")" + n);
							linear.add(-1, "f" + i + "(" + j + "," + m + ")" + n);

						}
					}
					linear.add(-1, "x" + i + j);
					NewProblem.add(linear, "<=", 0);

				}
			}
		}

		// 9
		for (int i = 1; i <= total_nodes; i++) {
			linear = new Linear();
			for (int j = 1; j <= total_nodes; j++) {
				for (int m = 1; m <= total_nodes; m++) {
					linear.add(-1, "f" + i + "(" + i + "," + m + ")" + j);
				}
				linear.add(1, "x" + i + j);

			}
			NewProblem.add(linear, "=", 0);
		}

		Solver solver = factory.get();
		Result result = solver.solve(NewProblem);
		int var_sum = 0;
		for (int i = 1; i <= total_nodes; i++) {
			var_sum = var_sum + result.get("z" + i + i).intValue();
		}
		System.out.println("Optimal Number of Hubs Located:"+var_sum);
		ArrayList<Integer> hubs=new ArrayList<Integer>();
		for (int i = 1; i <= total_nodes; i++) {
			if(result.get("z"+i+i).intValue()==1){
				hubs.add(i);

			}
		}
		System.out.println("Located hub nodes:"+hubs);
		System.out.println("-----------------------------------");
		for(int i=1;i<=clients.size();i++){
			for(int j=1; j<=hubs.size();j++){
				if(result.get("z"+clients.get(i-1)+hubs.get(j-1)).intValue()==1){
					System.out.println("Client demand at Node:"+clients.get(i-1)+" goes to Hub at Node:"+hubs.get(j-1));
				}
			}
		}
		System.out.println("-----------------------------------");
		System.out.println("Hub to Facility Optimal Routing:"+hubs.size());
        for(int i=1;i<=hubs.size();i++){
        	for(int j=1;j<=facilities.size();j++){
        		if(result.get("x"+hubs.get(i-1)+ facilities.get(j-1)).intValue()>0){

        			System.out.println("Hub at Node:"+hubs.get(i-1)+" routes demand to facility at Node:"+facilities.get(j-1));
        		}
        	}
        }
        
	}
}
