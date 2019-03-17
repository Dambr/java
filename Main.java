public class Main{
	private static double atanh(double x){
		return Math.log( Math.sqrt(1 - Math.pow(x, 2)) / (1 - x) );
	}
	private static double tanh(double x){
		return (Math.exp(2 * x) - 1) / (Math.exp(2 * x) + 1);
	}
	public static void main (String[] args){
		int N = 10;
		double [] cityx = {0.5, 0.24, 0.17, 0.23, 0.52, 0.87, 0.69, 0.85, 0.67, 0.62, 0.91};
		double [] cityy = {0.5, 0.15, 0.23, 0.76, 0.94, 0.65, 0.52, 0.36, 0.25, 0.26, 0.96};
		double [][] d = new double [cityx.length][cityy.length];
		double [][] Udao = new double [cityx.length][cityy.length];
		double [][] V = new double [cityx.length][cityy.length];
		for ( int i = 1; i <= N; i ++ ){
			for ( int j = 1; j <= N; j ++ ){
				d[i][j] = (double)Math.sqrt( Math.pow( (cityx[i] - cityx[j]) , 2) + Math.pow( cityy[i] - cityy[j] ,2) );
			}
		}
		int A = 500;
		int B = 500;
		int C = 1000;
		int D = 500;
		double u0 = 0.02;
		double lamda = 0.0001;
		int total = 0;
		int toend = 0;
		long start = System.currentTimeMillis();
		while (toend == 0) {
			total=total+1;
			for ( int i = 0; i < V.length; i ++ ){
				for ( int j = 0; j < V[i].length; j ++ ){
					V[i][j] = Math.random();
				}
			}
			double [][] U = new double [cityx.length][cityy.length];
			for ( int i = 0; i < U.length; i ++ ){
				for ( int j = 0; j < U.length; j ++ ){
					U[i][j] = atanh(2*V[i][j]-1) * u0;
				}
			}
			for ( int i = 1; i <= 200; i ++ ){
				for ( int ux = 1; ux <= N; ux ++ ){
					for ( int ui = 1; ui <= N; ui ++ ){
						double m1 = 0.0;
						double m2 = 0.0;
						double m3 = 0.0;
						double m4 = 0.0;
						for ( int j = 1; j <= N; j ++ ){
							if ( j != ui ){
								m1 = m1 + V[ux][j];
							}
						}
						m1 = -A * m1;
						for ( int y = 1; y <= N; y ++ ){
							if ( y != ux ){
								m2 = m2 + V[y][ui];
							}
						}
						m2 = -B * m2;
						for ( int x = 1; x <= N; x ++ ){
							for ( int j = 1; j <= N; j ++ ){
								m3 = m3 + V[x][j];
							}
						}
						m3 = -C * ( m3 - N );
						for ( int y = 1; y <= N; y ++ ){
							if ( y != ux ){
								if ( ui == 1 ){
									m4 = m4 + d[ux][y] * ( V[y][ui + 1] + V[y][N] );
								}
								else if ( ui == N ){
									m4 = m4 + d[ux][y] * ( V[y][ui - 1] + V[y][1] );
								}
								else {
									m4  = m4 + d[ux][y] * ( V[y][ui + 1] + V[y][ui - 1] );
								}
							}
						}
						m4 = -D * m4;	
						Udao[ux][ui] = -U[ux][ui] + m1 + m2 + m3 + m4;
					}
				}
				for ( int str = 0; str < U.length; str ++ ){
					for ( int stb = 0; stb < U[str].length; stb ++ ){
						U[str][stb] = U[str][stb] + lamda * Udao[str][stb];
					}
				}
				for ( int str = 0; str < V.length; str ++ ){
					for ( int stb = 0; stb < V[str].length; stb ++ ){
						V[str][stb] = ( 1 + tanh( U[str][stb]/u0 ) ) / 2;
					}
				}
				for ( int ux = 1; ux <= N; ux ++ ){
					for ( int ui = 1; ui <= N; ui ++ ){
						if ( V[ux][ui] < 0.3 ){
							V[ux][ui] = 0;
						}
						if ( V[ux][ui] > 0.7 ){
							V[ux][ui] = 1;
						}
					}
				}
			}
			System.out.println();
			for ( int str = 1; str < V.length; str ++ ){
				System.out.println();
				for ( int stb = 1; stb < V[str].length; stb ++ ){
					System.out.print(V[str][stb] + " ");
				}
			}
			double test1 = 0;
			for ( int ux = 1; ux <= N; ux ++ ){
				for ( int ui = 1; ui <= N; ui ++ ){
					test1 = test1 + V[ux][ui];
				}
			}
			double test2 = 0;
			for ( int x = 1; x <= N; x ++ ){
				for ( int i = 1; i <= N - 1; i ++ ){
					for ( int j = i + 1; j <= N; j ++ ){
						test2 = test2 + V[x][i] * V[x][j];
					}
				}
			}
			double test3 = 0;
			for ( int i = 1; i <= N; i ++ ){
				for ( int x = 1; x <= N - 1; x ++ ){
					for ( int y = x + 1; y <= N; y ++ ){
						test3 = test3 + V[x][i] * V[y][i];
					}
				}
			}
			if ( test1 == N && test2 == 0 && test3 == 0 ){
				toend = 1;
			}
			else{
				toend = 0;
			}
		}
		System.out.println();
		System.out.println();
		System.out.println("Calculation is completed for " + (System.currentTimeMillis() - start) / 1000 + " sec");
		System.out.println("Attempts: " + total);
		double [] cityx_final = new double [cityx.length + 2];
		double [] cityy_final = new double [cityy.length + 2];
		for ( int j = 1; j <= N; j ++ ){
			for ( int i = 1; i <= N; i ++ ){
				if ( V[i][j] == 1 ){
					cityx_final[j] = cityx[i];
					cityy_final[j] = cityy[i];
				}
			}
		}
		cityx_final[N + 1] = cityx_final[1];
		cityy_final[N + 1] = cityy_final[1];
		double td = 0;
		for ( int i = 1; i <= N - 1; i ++ ){
			td = td + Math.sqrt( Math.pow(( cityx_final[i] - cityx_final[i + 1] ) , 2) + Math.pow(( cityy_final[i] - cityy_final[i + 1] ) , 2) );
		}
		System.out.println();
		System.out.println("Length: " + td);
		int [] march = new int [cityx.length + 1];
		for ( int i = 0; i < cityx_final.length; i ++ ){
			for ( int j = 0; j < cityx.length; j ++ ){
				if ( cityx_final[i] == cityx[j] && cityy_final[i] == cityy[j] ){
					march[i] = j;
				}
			}
		}
		System.out.println();
		for ( int i = 1; i < march.length; i ++ ){
			System.out.print(march[i] + " ");
		}
		System.out.println();
	}
}