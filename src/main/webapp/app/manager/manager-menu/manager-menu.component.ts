import { Component, inject, signal, computed, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountRoleService } from 'app/core/profile/account-role.service';

interface Category {
  id: number;
  name: string;
  description?: string;
  displayOrder?: number;
  isActive?: boolean;
  brand?: { id: number };
}

interface MenuItem {
  id: number;
  name: string;
  description?: string;
  price?: number;
  discountedPrice?: number;
  imageUrl?: string;
  isAvailable?: boolean;
  isFeatured?: boolean;
  isVegetarian?: boolean;
  isVegan?: boolean;
  isGlutenFree?: boolean;
  spicyLevel?: number;
  calories?: number;
  preparationTimeMinutes?: number;
  displayOrder?: number;
  menuCategory?: { id: number; name?: string };
}

interface Location {
  id: number;
  name?: string;
  brand?: { id: number; name?: string };
}

@Component({
  selector: 'app-manager-menu',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="rv-admin-page">
      <div class="rv-admin-page-header">
        <div>
          <h1 class="rv-admin-page-title">Meniu restaurant</h1>
          <p class="rv-admin-page-subtitle">{{ brandName() }}</p>
        </div>
      </div>

      @if (isLoading()) {
        <div class="mm-loader">Se încarcă...</div>
      } @else {
        <div class="mm-layout">
          <!-- Left: Categories -->
          <div class="mm-categories glass">
            <div class="mm-section-header">
              <span class="mm-section-title">Categorii</span>
              <button class="mm-btn mm-btn--primary" (click)="openCategoryForm()">+ Adaugă</button>
            </div>

            @if (categories().length === 0) {
              <p class="mm-empty">Nicio categorie. Adaugă prima categorie.</p>
            } @else {
              <ul class="mm-cat-list">
                @for (cat of categories(); track cat.id) {
                  <li class="mm-cat-item" [class.mm-cat-item--active]="selectedCategoryId() === cat.id" (click)="selectCategory(cat)">
                    <span class="mm-cat-name">{{ cat.name }}</span>
                    <span class="mm-cat-badge" [class.mm-cat-badge--inactive]="!cat.isActive">
                      {{ cat.isActive !== false ? 'Activ' : 'Inactiv' }}
                    </span>
                    <div class="mm-cat-actions">
                      <button class="mm-icon-btn" title="Editează" (click)="editCategory(cat, $event)">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                        </svg>
                      </button>
                      <button class="mm-icon-btn mm-icon-btn--danger" title="Șterge" (click)="deleteCategory(cat, $event)">
                        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polyline points="3 6 5 6 21 6" />
                          <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
                          <path d="M10 11v6M14 11v6" />
                          <path d="M9 6V4h6v2" />
                        </svg>
                      </button>
                    </div>
                  </li>
                }
              </ul>
            }
          </div>

          <!-- Right: Items for selected category -->
          <div class="mm-items glass">
            @if (!selectedCategoryId()) {
              <div class="mm-hint">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2" opacity=".3">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                </svg>
                <p>Selectează o categorie pentru a vedea și gestiona produsele</p>
              </div>
            } @else {
              <div class="mm-section-header">
                <span class="mm-section-title">Produse – {{ selectedCategoryName() }}</span>
                <button class="mm-btn mm-btn--primary" (click)="openItemForm()">+ Produs nou</button>
              </div>

              @if (isLoadingItems()) {
                <p class="mm-empty">Se încarcă produsele...</p>
              } @else if (items().length === 0) {
                <p class="mm-empty">Niciun produs în această categorie.</p>
              } @else {
                <div class="mm-items-grid">
                  @for (item of items(); track item.id) {
                    <div class="mm-item-card" [class.mm-item-card--unavailable]="!item.isAvailable">
                      @if (item.imageUrl) {
                        <img [src]="item.imageUrl" class="mm-item-img" [alt]="item.name" />
                      } @else {
                        <div class="mm-item-img mm-item-img--placeholder">
                          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" opacity=".3">
                            <rect x="3" y="3" width="18" height="18" rx="2" />
                            <circle cx="8.5" cy="8.5" r="1.5" />
                            <polyline points="21 15 16 10 5 21" />
                          </svg>
                        </div>
                      }
                      <div class="mm-item-body">
                        <div class="mm-item-name">{{ item.name }}</div>
                        @if (item.description) {
                          <div class="mm-item-desc">{{ item.description }}</div>
                        }
                        <div class="mm-item-meta">
                          <span class="mm-item-price">{{ item.price | number: '1.2-2' }} MDL</span>
                          @if (item.isVegetarian) {
                            <span class="mm-tag mm-tag--green">🌱</span>
                          }
                          @if (item.isVegan) {
                            <span class="mm-tag mm-tag--green">🌿</span>
                          }
                          @if (item.isGlutenFree) {
                            <span class="mm-tag mm-tag--amber">GF</span>
                          }
                          @if (item.spicyLevel && item.spicyLevel > 0) {
                            <span class="mm-tag mm-tag--red">{{ '🌶'.repeat(item.spicyLevel) }}</span>
                          }
                        </div>
                        <div class="mm-item-actions">
                          <button class="mm-toggle-btn" [class.mm-toggle-btn--on]="item.isAvailable" (click)="toggleAvailability(item)">
                            {{ item.isAvailable ? 'Disponibil' : 'Indisponibil' }}
                          </button>
                          <button class="mm-icon-btn" title="Editează" (click)="editItem(item)">
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                            </svg>
                          </button>
                          <button class="mm-icon-btn mm-icon-btn--danger" title="Șterge" (click)="deleteItem(item)">
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <polyline points="3 6 5 6 21 6" />
                              <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
                              <path d="M10 11v6M14 11v6" />
                              <path d="M9 6V4h6v2" />
                            </svg>
                          </button>
                        </div>
                      </div>
                    </div>
                  }
                </div>
              }
            }
          </div>
        </div>
      }

      <!-- Category Modal -->
      @if (showCategoryModal()) {
        <div class="mm-modal-backdrop" (click)="closeCategoryModal()">
          <div class="mm-modal glass" (click)="$event.stopPropagation()">
            <h3 class="mm-modal-title">{{ editingCategory() ? 'Editează categorie' : 'Categorie nouă' }}</h3>
            <div class="rv-form-group">
              <label>Nume *</label>
              <input class="rv-input" [(ngModel)]="catForm.name" placeholder="ex: Aperitive" />
            </div>
            <div class="rv-form-group">
              <label>Descriere</label>
              <textarea class="rv-input" rows="2" [(ngModel)]="catForm.description" placeholder="Descriere opțională"></textarea>
            </div>
            <div class="rv-form-group">
              <label>Ordine afișare</label>
              <input class="rv-input" type="number" [(ngModel)]="catForm.displayOrder" />
            </div>
            <div class="mm-modal-check">
              <input type="checkbox" id="catActive" [(ngModel)]="catForm.isActive" />
              <label for="catActive">Categorie activă</label>
            </div>
            <div class="mm-modal-actions">
              <button class="mm-btn mm-btn--ghost" (click)="closeCategoryModal()">Anulează</button>
              <button class="mm-btn mm-btn--primary" (click)="saveCategory()" [disabled]="!catForm.name">
                {{ isSaving() ? 'Se salvează...' : 'Salvează' }}
              </button>
            </div>
          </div>
        </div>
      }

      <!-- Item Modal -->
      @if (showItemModal()) {
        <div class="mm-modal-backdrop" (click)="closeItemModal()">
          <div class="mm-modal mm-modal--wide glass" (click)="$event.stopPropagation()">
            <h3 class="mm-modal-title">{{ editingItem() ? 'Editează produs' : 'Produs nou' }}</h3>
            <div class="mm-form-grid">
              <div class="rv-form-group">
                <label>Nume *</label>
                <input class="rv-input" [(ngModel)]="itemForm.name" placeholder="ex: Salată Caesar" />
              </div>
              <div class="rv-form-group">
                <label>Preț (MDL) *</label>
                <input class="rv-input" type="number" step="0.01" [(ngModel)]="itemForm.price" />
              </div>
              <div class="rv-form-group">
                <label>Preț redus (MDL)</label>
                <input class="rv-input" type="number" step="0.01" [(ngModel)]="itemForm.discountedPrice" />
              </div>
              <div class="rv-form-group">
                <label>Timp preparare (min)</label>
                <input class="rv-input" type="number" [(ngModel)]="itemForm.preparationTimeMinutes" />
              </div>
              <div class="rv-form-group">
                <label>Calorii</label>
                <input class="rv-input" type="number" [(ngModel)]="itemForm.calories" />
              </div>
              <div class="rv-form-group">
                <label>Nivel picant (0-3)</label>
                <select class="rv-input" [(ngModel)]="itemForm.spicyLevel">
                  <option [value]="0">0 – Nu picant</option>
                  <option [value]="1">1 – Ușor</option>
                  <option [value]="2">2 – Mediu</option>
                  <option [value]="3">3 – Picant</option>
                </select>
              </div>
              <div class="rv-form-group mm-form-span">
                <label>Descriere</label>
                <textarea
                  class="rv-input"
                  rows="2"
                  [(ngModel)]="itemForm.description"
                  placeholder="Ingrediente sau descriere scurtă"
                ></textarea>
              </div>
              <div class="rv-form-group mm-form-span">
                <label>URL imagine</label>
                <input class="rv-input" [(ngModel)]="itemForm.imageUrl" placeholder="https://..." />
              </div>
            </div>
            <div class="mm-checkboxes">
              <label class="mm-check-item"><input type="checkbox" [(ngModel)]="itemForm.isAvailable" /> Disponibil</label>
              <label class="mm-check-item"><input type="checkbox" [(ngModel)]="itemForm.isFeatured" /> Featured</label>
              <label class="mm-check-item"><input type="checkbox" [(ngModel)]="itemForm.isVegetarian" /> Vegetarian</label>
              <label class="mm-check-item"><input type="checkbox" [(ngModel)]="itemForm.isVegan" /> Vegan</label>
              <label class="mm-check-item"><input type="checkbox" [(ngModel)]="itemForm.isGlutenFree" /> Fără gluten</label>
            </div>
            <div class="mm-modal-actions">
              <button class="mm-btn mm-btn--ghost" (click)="closeItemModal()">Anulează</button>
              <button class="mm-btn mm-btn--primary" (click)="saveItem()" [disabled]="!itemForm.name || isSaving()">
                {{ isSaving() ? 'Se salvează...' : 'Salvează' }}
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [
    `
      .mm-layout {
        display: grid;
        grid-template-columns: 280px 1fr;
        gap: 20px;
        align-items: start;
      }
      .mm-categories,
      .mm-items {
        padding: 16px;
        border-radius: 12px;
      }
      .mm-section-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 14px;
      }
      .mm-section-title {
        font-size: 0.85rem;
        font-weight: 700;
        text-transform: uppercase;
        letter-spacing: 0.04em;
        color: rgba(255, 255, 255, 0.7);
      }
      .mm-empty {
        font-size: 0.82rem;
        color: rgba(255, 255, 255, 0.3);
        padding: 20px 0;
        text-align: center;
      }
      .mm-hint {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
        padding: 60px 20px;
        color: rgba(255, 255, 255, 0.3);
        font-size: 0.82rem;
        text-align: center;
      }
      .mm-loader {
        color: rgba(255, 255, 255, 0.4);
        padding: 40px;
        text-align: center;
      }
      .mm-cat-list {
        list-style: none;
        margin: 0;
        padding: 0;
        display: flex;
        flex-direction: column;
        gap: 4px;
      }
      .mm-cat-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 9px 10px;
        border-radius: 8px;
        cursor: pointer;
        background: rgba(255, 255, 255, 0.03);
        border: 1px solid rgba(255, 255, 255, 0.06);
        transition: background 0.15s;
        &:hover {
          background: rgba(255, 255, 255, 0.07);
        }
        &--active {
          background: rgba(245, 165, 32, 0.1) !important;
          border-color: rgba(245, 165, 32, 0.25);
        }
      }
      .mm-cat-name {
        flex: 1;
        font-size: 0.82rem;
        font-weight: 500;
      }
      .mm-cat-badge {
        font-size: 0.65rem;
        padding: 2px 6px;
        border-radius: 4px;
        background: rgba(34, 197, 94, 0.15);
        color: #22c55e;
        &--inactive {
          background: rgba(239, 68, 68, 0.15);
          color: #ef4444;
        }
      }
      .mm-cat-actions {
        display: flex;
        gap: 4px;
        opacity: 0;
        transition: opacity 0.15s;
      }
      .mm-cat-item:hover .mm-cat-actions {
        opacity: 1;
      }
      .mm-icon-btn {
        background: none;
        border: none;
        color: rgba(255, 255, 255, 0.4);
        cursor: pointer;
        padding: 3px;
        border-radius: 4px;
        line-height: 0;
        transition: color 0.15s;
        &:hover {
          color: #fff;
        }
        &--danger:hover {
          color: #ef4444;
        }
      }
      .mm-btn {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        padding: 6px 14px;
        border-radius: 8px;
        font-size: 0.8rem;
        font-weight: 600;
        border: none;
        cursor: pointer;
        transition: all 0.15s;
        &--primary {
          background: rgba(245, 165, 32, 0.15);
          border: 1px solid rgba(245, 165, 32, 0.3);
          color: var(--rv-orange, #f5a520);
          &:hover {
            background: rgba(245, 165, 32, 0.25);
          }
          &:disabled {
            opacity: 0.5;
            cursor: not-allowed;
          }
        }
        &--ghost {
          background: rgba(255, 255, 255, 0.06);
          border: 1px solid rgba(255, 255, 255, 0.1);
          color: rgba(255, 255, 255, 0.6);
          &:hover {
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
          }
        }
        &--danger {
          background: rgba(239, 68, 68, 0.1);
          border: 1px solid rgba(239, 68, 68, 0.25);
          color: #ef4444;
          &:hover {
            background: rgba(239, 68, 68, 0.2);
          }
        }
      }
      .mm-items-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
        gap: 12px;
      }
      .mm-item-card {
        background: rgba(255, 255, 255, 0.04);
        border: 1px solid rgba(255, 255, 255, 0.08);
        border-radius: 10px;
        overflow: hidden;
        transition: border-color 0.15s;
        &:hover {
          border-color: rgba(255, 255, 255, 0.15);
        }
        &--unavailable {
          opacity: 0.5;
        }
      }
      .mm-item-img {
        width: 100%;
        height: 100px;
        object-fit: cover;
        display: block;
        &--placeholder {
          display: flex;
          align-items: center;
          justify-content: center;
          background: rgba(255, 255, 255, 0.04);
          height: 100px;
        }
      }
      .mm-item-body {
        padding: 10px 12px;
      }
      .mm-item-name {
        font-size: 0.85rem;
        font-weight: 600;
        margin-bottom: 3px;
      }
      .mm-item-desc {
        font-size: 0.72rem;
        color: rgba(255, 255, 255, 0.45);
        margin-bottom: 6px;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
      .mm-item-meta {
        display: flex;
        align-items: center;
        gap: 6px;
        margin-bottom: 8px;
      }
      .mm-item-price {
        font-weight: 700;
        color: var(--rv-orange, #f5a520);
        font-size: 0.85rem;
      }
      .mm-tag {
        font-size: 0.7rem;
        padding: 1px 5px;
        border-radius: 4px;
        &--green {
          background: rgba(34, 197, 94, 0.15);
          color: #22c55e;
        }
        &--amber {
          background: rgba(245, 158, 11, 0.15);
          color: #f59e0b;
        }
        &--red {
          background: rgba(239, 68, 68, 0.15);
          color: #ef4444;
        }
      }
      .mm-item-actions {
        display: flex;
        align-items: center;
        gap: 6px;
      }
      .mm-toggle-btn {
        flex: 1;
        padding: 4px 8px;
        border-radius: 6px;
        font-size: 0.7rem;
        font-weight: 600;
        border: 1px solid;
        cursor: pointer;
        transition: all 0.15s;
        background: rgba(239, 68, 68, 0.1);
        border-color: rgba(239, 68, 68, 0.25);
        color: #ef4444;
        &--on {
          background: rgba(34, 197, 94, 0.1);
          border-color: rgba(34, 197, 94, 0.25);
          color: #22c55e;
        }
      }
      /* Modal */
      .mm-modal-backdrop {
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.6);
        z-index: 1000;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 20px;
      }
      .mm-modal {
        padding: 24px;
        border-radius: 14px;
        width: 100%;
        max-width: 440px;
        max-height: 90vh;
        overflow-y: auto;
        &--wide {
          max-width: 640px;
        }
      }
      .mm-modal-title {
        font-size: 1rem;
        font-weight: 700;
        margin-bottom: 18px;
        color: rgba(255, 255, 255, 0.9);
      }
      .mm-modal-check {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 16px;
        font-size: 0.82rem;
        color: rgba(255, 255, 255, 0.7);
      }
      .mm-modal-actions {
        display: flex;
        gap: 10px;
        justify-content: flex-end;
        margin-top: 18px;
      }
      .mm-form-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 0 16px;
      }
      .mm-form-span {
        grid-column: 1 / -1;
      }
      .mm-checkboxes {
        display: flex;
        gap: 14px;
        flex-wrap: wrap;
        margin-bottom: 16px;
      }
      .mm-check-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 0.8rem;
        color: rgba(255, 255, 255, 0.7);
        cursor: pointer;
      }
      @media (max-width: 900px) {
        .mm-layout {
          grid-template-columns: 1fr;
        }
      }
      @media (max-width: 600px) {
        .mm-form-grid {
          grid-template-columns: 1fr;
        }
      }
    `,
  ],
})
export default class ManagerMenuComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly roleService = inject(AccountRoleService);

  isLoading = signal(true);
  isLoadingItems = signal(false);
  isSaving = signal(false);
  categories = signal<Category[]>([]);
  items = signal<MenuItem[]>([]);
  selectedCategoryId = signal<number | null>(null);
  brandId = signal<number | null>(null);
  brandName = signal<string>('');

  showCategoryModal = signal(false);
  showItemModal = signal(false);
  editingCategory = signal<Category | null>(null);
  editingItem = signal<MenuItem | null>(null);

  catForm: { name: string; description: string; displayOrder: number; isActive: boolean } = {
    name: '',
    description: '',
    displayOrder: 0,
    isActive: true,
  };
  itemForm: Partial<MenuItem> & { name: string; price: number } = this.emptyItemForm();

  selectedCategoryName = computed(() => {
    const cat = this.categories().find(c => c.id === this.selectedCategoryId());
    return cat?.name ?? '';
  });

  ngOnInit(): void {
    this.roleService.load();
    const locId = this.roleService.locationId();
    if (locId) {
      this.loadLocationBrand(locId);
    } else {
      // wait a bit for role service to load
      setTimeout(() => {
        const id = this.roleService.locationId();
        if (id) this.loadLocationBrand(id);
        else this.isLoading.set(false);
      }, 800);
    }
  }

  private loadLocationBrand(locationId: number): void {
    this.http.get<Location>(this.configService.getEndpointFor(`api/locations/${locationId}`)).subscribe({
      next: loc => {
        if (loc.brand?.id) {
          this.brandId.set(loc.brand.id);
          this.brandName.set(loc.brand.name ?? loc.name ?? '');
          this.loadCategories(loc.brand.id);
        } else {
          this.isLoading.set(false);
        }
      },
      error: () => this.isLoading.set(false),
    });
  }

  private loadCategories(brandId: number): void {
    this.http
      .get<Category[]>(this.configService.getEndpointFor(`api/menu-categories?brandId.equals=${brandId}&size=200&sort=displayOrder,asc`))
      .subscribe({
        next: cats => {
          this.categories.set(cats);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false),
      });
  }

  selectCategory(cat: Category): void {
    this.selectedCategoryId.set(cat.id);
    this.loadItems(cat.id);
  }

  private loadItems(categoryId: number): void {
    this.isLoadingItems.set(true);
    this.http
      .get<
        MenuItem[]
      >(this.configService.getEndpointFor(`api/menu-items?menuCategoryId.equals=${categoryId}&size=200&sort=displayOrder,asc`))
      .subscribe({
        next: items => {
          this.items.set(items);
          this.isLoadingItems.set(false);
        },
        error: () => this.isLoadingItems.set(false),
      });
  }

  // ── Category CRUD ─────────────────────────────────────────────────────────
  openCategoryForm(): void {
    this.editingCategory.set(null);
    this.catForm = { name: '', description: '', displayOrder: this.categories().length, isActive: true };
    this.showCategoryModal.set(true);
  }

  editCategory(cat: Category, event: Event): void {
    event.stopPropagation();
    this.editingCategory.set(cat);
    this.catForm = {
      name: cat.name,
      description: cat.description ?? '',
      displayOrder: cat.displayOrder ?? 0,
      isActive: cat.isActive !== false,
    };
    this.showCategoryModal.set(true);
  }

  closeCategoryModal(): void {
    this.showCategoryModal.set(false);
  }

  saveCategory(): void {
    const bId = this.brandId();
    if (!bId || !this.catForm.name) return;
    this.isSaving.set(true);

    const editing = this.editingCategory();
    const body: Partial<Category> & { brand: { id: number } } = {
      name: this.catForm.name,
      description: this.catForm.description || undefined,
      displayOrder: this.catForm.displayOrder,
      isActive: this.catForm.isActive,
      brand: { id: bId },
    };

    const req = editing
      ? this.http.put<Category>(this.configService.getEndpointFor(`api/menu-categories/${editing.id}`), { ...body, id: editing.id })
      : this.http.post<Category>(this.configService.getEndpointFor('api/menu-categories'), body);

    req.subscribe({
      next: () => {
        this.isSaving.set(false);
        this.showCategoryModal.set(false);
        this.loadCategories(bId);
      },
      error: () => this.isSaving.set(false),
    });
  }

  deleteCategory(cat: Category, event: Event): void {
    event.stopPropagation();
    if (!confirm(`Ștergi categoria "${cat.name}"? Se vor șterge și produsele asociate.`)) return;
    this.http.delete(this.configService.getEndpointFor(`api/menu-categories/${cat.id}`)).subscribe({
      next: () => {
        if (this.selectedCategoryId() === cat.id) {
          this.selectedCategoryId.set(null);
          this.items.set([]);
        }
        const bId = this.brandId();
        if (bId) this.loadCategories(bId);
      },
      error: () => alert('Eroare la ștergere. Poate categoria are produse asociate.'),
    });
  }

  // ── Item CRUD ─────────────────────────────────────────────────────────────
  openItemForm(): void {
    this.editingItem.set(null);
    this.itemForm = this.emptyItemForm();
    this.showItemModal.set(true);
  }

  editItem(item: MenuItem): void {
    this.editingItem.set(item);
    this.itemForm = {
      name: item.name,
      description: item.description ?? '',
      price: item.price ?? 0,
      discountedPrice: item.discountedPrice,
      imageUrl: item.imageUrl ?? '',
      isAvailable: item.isAvailable !== false,
      isFeatured: item.isFeatured ?? false,
      isVegetarian: item.isVegetarian ?? false,
      isVegan: item.isVegan ?? false,
      isGlutenFree: item.isGlutenFree ?? false,
      spicyLevel: item.spicyLevel ?? 0,
      calories: item.calories,
      preparationTimeMinutes: item.preparationTimeMinutes,
      displayOrder: item.displayOrder ?? 0,
    };
    this.showItemModal.set(true);
  }

  closeItemModal(): void {
    this.showItemModal.set(false);
  }

  saveItem(): void {
    const catId = this.selectedCategoryId();
    if (!catId || !this.itemForm.name) return;
    this.isSaving.set(true);

    const editing = this.editingItem();
    const body = {
      ...(editing ? { id: editing.id } : {}),
      name: this.itemForm.name,
      description: this.itemForm.description || undefined,
      price: this.itemForm.price ?? 0,
      discountedPrice: this.itemForm.discountedPrice || undefined,
      imageUrl: this.itemForm.imageUrl || undefined,
      isAvailable: this.itemForm.isAvailable !== false,
      isFeatured: this.itemForm.isFeatured ?? false,
      isVegetarian: this.itemForm.isVegetarian ?? false,
      isVegan: this.itemForm.isVegan ?? false,
      isGlutenFree: this.itemForm.isGlutenFree ?? false,
      spicyLevel: this.itemForm.spicyLevel ?? 0,
      calories: this.itemForm.calories || undefined,
      preparationTimeMinutes: this.itemForm.preparationTimeMinutes || undefined,
      displayOrder: this.itemForm.displayOrder ?? 0,
      menuCategory: { id: catId },
    };

    const req = editing
      ? this.http.put<MenuItem>(this.configService.getEndpointFor(`api/menu-items/${editing.id}`), body)
      : this.http.post<MenuItem>(this.configService.getEndpointFor('api/menu-items'), body);

    req.subscribe({
      next: () => {
        this.isSaving.set(false);
        this.showItemModal.set(false);
        this.loadItems(catId);
      },
      error: () => this.isSaving.set(false),
    });
  }

  toggleAvailability(item: MenuItem): void {
    const newVal = !item.isAvailable;
    this.http
      .patch<MenuItem>(this.configService.getEndpointFor(`api/menu-items/${item.id}`), { id: item.id, isAvailable: newVal })
      .subscribe({
        next: updated => {
          this.items.update(list => list.map(i => (i.id === item.id ? { ...i, isAvailable: newVal } : i)));
        },
        error: () => {},
      });
  }

  deleteItem(item: MenuItem): void {
    if (!confirm(`Ștergi produsul "${item.name}"?`)) return;
    this.http.delete(this.configService.getEndpointFor(`api/menu-items/${item.id}`)).subscribe({
      next: () => {
        const catId = this.selectedCategoryId();
        if (catId) this.loadItems(catId);
      },
      error: () => alert('Eroare la ștergere.'),
    });
  }

  private emptyItemForm(): Partial<MenuItem> & { name: string; price: number } {
    return {
      name: '',
      price: 0,
      description: '',
      imageUrl: '',
      isAvailable: true,
      isFeatured: false,
      isVegetarian: false,
      isVegan: false,
      isGlutenFree: false,
      spicyLevel: 0,
    };
  }
}
